package ru.radom.kabinet.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.services.registrator.RegistratorDataService;
import ru.radom.kabinet.dao.SharerDao;
import ru.radom.kabinet.dao.communities.CommunityDao;
import ru.radom.kabinet.dao.fields.FieldDao;
import ru.radom.kabinet.dao.fields.FieldValueDao;
import ru.radom.kabinet.dao.registration.RegistratorDao;
import ru.radom.kabinet.model.ProgressInfo;
import ru.radom.kabinet.model.ProgressStatus;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.fields.FieldEntity;
import ru.radom.kabinet.model.fields.FieldValueEntity;
import ru.radom.kabinet.model.utils.Similarities;
import ru.radom.kabinet.model.utils.SupportAddress;
import ru.radom.kabinet.model.utils.YandexGeocoderResponse;
import ru.radom.kabinet.utils.YandexGeocoder;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service("adminDataService")
public class AdminDataService {
    // Допустимое значение совпадающих полей, при котором считается, что адреса похожи
    private static final int PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES = 4;

    @Autowired
    private SupportAddressService supportAddressService;

    @Autowired
    private SharerDao sharerDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private FieldDao fieldDao;

    @Autowired
    private FieldValueDao fieldValueDao;

    @Autowired
    private RegistratorDataService registratorDataService;

    // Фикс координат
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private AtomicInteger repairGeoPositionsStatus = new AtomicInteger(-1); // -1 остановленн, [0-100] статус выполнения в процентах

    public ProgressInfo getRepairGeoPositionsStatus() {
        int status = repairGeoPositionsStatus.get();
        String name = "repairGeoPositionsStatus";

        ProgressInfo info;
        if (status != -1) {
            info = new ProgressInfo(ProgressStatus.RUNNING, status, name);
        } else {
            info = new ProgressInfo(ProgressStatus.STOPPED, 0, name);
        }
        return info;
    }

    @Async
    public void repairGeoPositions() {
        try {
            int status = repairGeoPositionsStatus.get();
            if (status == -1) {
                repairGeoPositionsStatus.set(0);
                repairGeoPositionsTask();
                repairGeoPositionsStatus.set(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            repairGeoPositionsStatus.set(-1);
        }
    }

    private void repairGeoPositionsTask() {
        try {
            int progress = 0;
            int lastProgress = progress;
            //List<Sharer> sharers = sharerDao.getNotDeleted();

            List<UserEntity> userEntities = sharerDao.getAllVerified();
            System.out.println("Найдено " + userEntities.size() + " идентифицированных пользователей подлежащих коррекции");

            for (int i = 0, l = userEntities.size(); i < l; i++) {
                UserEntity userEntity = userEntities.get(i);

                System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                System.out.println("[" + i + "] Коррекция координат для " + userEntity);

                repairGeoPosition(userEntity);
                progress = i * 100 / l;
                if (progress != lastProgress) {
                    lastProgress = progress;
                    repairGeoPositionsStatus.set(progress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGeoPosition(UserEntity userEntity, FieldEntity field, String geoPosition) {
        FieldValueEntity geoPositionFieldValue = fieldValueDao.get(userEntity, field);
        if (geoPositionFieldValue == null) {
            geoPositionFieldValue = new FieldValueEntity();
            geoPositionFieldValue.setField(field);
            geoPositionFieldValue.setHidden(field.isHiddenByDefault());
            geoPositionFieldValue.setObject(userEntity);
        }
        geoPositionFieldValue.setStringValue(geoPosition);
        fieldValueDao.saveOrUpdate(geoPositionFieldValue);
    }

    private void repairRegistratorOfficeGeoPosition(UserEntity userEntity) {
        try {
            System.out.println("[REGISTRATOR] * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            SupportAddress sharerSupportAddress = supportAddressService.getRegistratorOfficeSupportAddress(userEntity);

            YandexGeocoderResponse response = YandexGeocoder.doGeocoderRequest(sharerSupportAddress.getJoinedAddressBase());
            SupportAddress yandexSupportAddress = supportAddressService.getSupportAddressFromYandexGeocoderResponse(response);

            SupportAddress mergedSupportAddress = mergeAddresses(sharerSupportAddress, yandexSupportAddress);

            if (mergedSupportAddress.isReliableByCoordinates()) {
                System.out.println("Найденый адрес является достоверным");
            } else {
                System.out.println("Найденый адрес не является достоверным, могут быть неточности");
            }

            System.out.println("search          [" + sharerSupportAddress.getJoinedAddressBase() + "]");
            System.out.println("original        [" + sharerSupportAddress.getGeoPosition() + "] [" + sharerSupportAddress.getGeoLocation() + "]");
            System.out.println("joined original [" + sharerSupportAddress.getJoinedAddress() + "]");
            System.out.println("yandex          [" + yandexSupportAddress.getGeoPosition() + "] [" + yandexSupportAddress.getGeoLocation() + "]");
            System.out.println("joined yandex   [" + yandexSupportAddress.getJoinedAddress() + "]");
            System.out.println("result          [" + mergedSupportAddress.getJoinedAddress() + "]");

            System.out.println("- - - - -");

            System.out.println("Координаты данного адреса сейчас будут автоматически скорректированны...");

            FieldEntity geoPositionField = fieldDao.getByInternalName("REGISTRATOR_OFFICE_GEO_POSITION");
            setGeoPosition(userEntity, geoPositionField, yandexSupportAddress.getGeoPosition());

            System.out.println("Коррекция из [" + sharerSupportAddress.getGeoPosition() + "] в [" + yandexSupportAddress.getGeoPosition() + "] успешно выполненна!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void repairGeoPosition(UserEntity userEntity) {
        try {
            //repairActualGeoPosition(sharer);
            //repairRegistrationGeoPosition(sharer);
            String registratorLevel = registratorDataService.getRegistratorLevel(userEntity.getId());
            if (!StringUtils.isBlank(registratorLevel)) {
                repairRegistratorOfficeGeoPosition(userEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Фактический адрес
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private AtomicInteger repairActualAddressesStatus = new AtomicInteger(-1); // -1 остановленн, [0-100] статус выполнения в процентах

    public ProgressInfo getRepairActualAddressesStatus() {
        int status = repairActualAddressesStatus.get();
        String name = "repairActualAddressesStatus";

        ProgressInfo info;
        if (status != -1) {
            info = new ProgressInfo(ProgressStatus.RUNNING, status, name);
        } else {
            info = new ProgressInfo(ProgressStatus.STOPPED, 0, name);
        }
        return info;
    }

    @Async
    public void repairActualAddresses() {
        try {
            int status = repairActualAddressesStatus.get();
            if (status == -1) {
                repairActualAddressesStatus.set(0);
                repairActualAddressesTask();
                repairActualAddressesStatus.set(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            repairActualAddressesStatus.set(-1);
        }
    }

    private void repairActualAddressesTask() {
        try {
            int progress = 0;
            int lastProgress = progress;
            //List<Sharer> sharers = sharerDao.getNotDeleted();
            List<UserEntity> userEntities = sharerDao.getAllVerified();

            for (int i = 0, l = userEntities.size(); i < l; i++) {
                UserEntity userEntity = userEntities.get(i);
                repairActualAddress(userEntity);
                progress = i * 100 / l;
                if (progress != lastProgress) {
                    lastProgress = progress;
                    repairActualAddressesStatus.set(progress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void repairActualAddress(UserEntity userEntity) {
        try {
            System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            System.out.println("Данные системы:");
            SupportAddress sharerSupportAddress = supportAddressService.getActualSupportAddress(userEntity);

            YandexGeocoderResponse response = YandexGeocoder.doGeocoderRequest(sharerSupportAddress.getJoinedAddressBase());
            System.out.println("Данные яндекса:");
            SupportAddress yandexSupportAddress = supportAddressService.getSupportAddressFromYandexGeocoderResponse(response);

            SupportAddress mergedSupportAddress = mergeAddresses(sharerSupportAddress, yandexSupportAddress);

            System.out.println("search          [" + sharerSupportAddress.getJoinedAddressBase() + "]");
            System.out.println("original        [" + sharerSupportAddress.getGeoPosition() + "] [" + sharerSupportAddress.getGeoLocation() + "]");
            System.out.println("joined original [" + sharerSupportAddress.getJoinedAddress() + "]");
            System.out.println("yandex          [" + yandexSupportAddress.getGeoPosition() + "] [" + yandexSupportAddress.getGeoLocation() + "]");
            System.out.println("joined yandex   [" + yandexSupportAddress.getJoinedAddress() + "]");
            System.out.println("result          [" + mergedSupportAddress.getJoinedAddress(true) + "]");
            System.out.println("location        [" + mergedSupportAddress.getFullAddress() + "]");

            System.out.println("---");

            if (mergedSupportAddress.isReliableByCoordinates()) {
                System.out.println("Совпадение по координатам. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveSharerActualSupportAddress(userEntity,mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().isAllMatches() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по полям. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveSharerActualSupportAddress(userEntity,mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(норма). Автоматическая коррекция выполненна не будет.");
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() < PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(ниже нормы). Автоматическая коррекция выполненна не будет.");
            } else {
                System.out.println("Совпаденй не обнаружено. Автоматическая коррекция выполненна не будет.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Адрес регистрации
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private AtomicInteger repairRegistrationAddressesStatus = new AtomicInteger(-1); // -1 остановленн, [0-100] статус выполнения в процентах

    public ProgressInfo getRepairRegistrationAddressesStatus() {
        int status = repairRegistrationAddressesStatus.get();
        String name = "repairRegistrationAddressesStatus";

        ProgressInfo info;
        if (status != -1) {
            info = new ProgressInfo(ProgressStatus.RUNNING, status, name);
        } else {
            info = new ProgressInfo(ProgressStatus.STOPPED, 0, name);
        }
        return info;
    }

    @Async
    public void repairRegistrationAddresses() {
        try {
            int status = repairRegistrationAddressesStatus.get();
            if (status == -1) {
                repairRegistrationAddressesStatus.set(0);
                repairRegistrationAddressesTask();
                repairRegistrationAddressesStatus.set(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            repairRegistrationAddressesStatus.set(-1);
        }
    }

    private void repairRegistrationAddressesTask() {
        try {
            int progress = 0;
            int lastProgress = progress;
            //List<Sharer> sharers = sharerDao.getNotDeleted();
            List<UserEntity> userEntities = sharerDao.getAllVerified();

            for (int i = 0, l = userEntities.size(); i < l; i++) {
                UserEntity userEntity = userEntities.get(i);
                repairRegistrationAddress(userEntity);
                progress = i * 100 / l;
                if (progress != lastProgress) {
                    lastProgress = progress;
                    repairRegistrationAddressesStatus.set(progress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void repairRegistrationAddress(UserEntity userEntity) {
        try {
            System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
            System.out.println("Данные системы:");
            SupportAddress sharerSupportAddress = supportAddressService.getRegistrationSupportAddress(userEntity);

            YandexGeocoderResponse response = YandexGeocoder.doGeocoderRequest(sharerSupportAddress.getJoinedAddressBase());
            System.out.println("Данные яндекса:");
            SupportAddress yandexSupportAddress = supportAddressService.getSupportAddressFromYandexGeocoderResponse(response);

            SupportAddress mergedSupportAddress = mergeAddresses(sharerSupportAddress, yandexSupportAddress);

            System.out.println("search          [" + sharerSupportAddress.getJoinedAddressBase() + "]");
            System.out.println("original        [" + sharerSupportAddress.getGeoPosition() + "] [" + sharerSupportAddress.getGeoLocation() + "]");
            System.out.println("joined original [" + sharerSupportAddress.getJoinedAddress() + "]");
            System.out.println("yandex          [" + yandexSupportAddress.getGeoPosition() + "] [" + yandexSupportAddress.getGeoLocation() + "]");
            System.out.println("joined yandex   [" + yandexSupportAddress.getJoinedAddress() + "]");
            System.out.println("result          [" + mergedSupportAddress.getJoinedAddress(true) + "]");
            System.out.println("location        [" + mergedSupportAddress.getFullAddress() + "]");

            System.out.println("---");

            if (mergedSupportAddress.isReliableByCoordinates()) {
                System.out.println("Совпадение по координатам. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveSharerRegistrationSupportAddress(userEntity, mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().isAllMatches() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по полям. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveSharerRegistrationSupportAddress(userEntity, mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(норма). Автоматическая коррекция выполненна не будет.");
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() < PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(ниже нормы). Автоматическая коррекция выполненна не будет.");
            } else {
                System.out.println("Совпаденй не обнаружено. Автоматическая коррекция выполненна не будет.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Адрес офиса регистраторов
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private AtomicInteger repairRegistratorAddressesStatus = new AtomicInteger(-1); // -1 остановленн, [0-100] статус выполнения в процентах

    public ProgressInfo getRepairRegistratorAddressesStatus() {
        int status = repairRegistratorAddressesStatus.get();
        String name = "repairRegistratorAddressesStatus";

        ProgressInfo info;
        if (status != -1) {
            info = new ProgressInfo(ProgressStatus.RUNNING, status, name);
        } else {
            info = new ProgressInfo(ProgressStatus.STOPPED, 0, name);
        }
        return info;
    }

    @Async
    public void repairRegistratorAddresses() {
        try {
            int status = repairRegistratorAddressesStatus.get();
            if (status == -1) {
                repairRegistratorAddressesStatus.set(0);
                repairRegistratorAddressesTask();
                repairRegistratorAddressesStatus.set(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            repairRegistratorAddressesStatus.set(-1);
        }
    }

    private void repairRegistratorAddressesTask() {
        try {
            int progress = 0;
            int lastProgress = progress;
            // TODO тянуть только регистраторов
            //List<Sharer> sharers = sharerDao.getNotDeleted();
            List<UserEntity> userEntities = sharerDao.getAllVerified();

            for (int i = 0, l = userEntities.size(); i < l; i++) {
                UserEntity userEntity = userEntities.get(i);
                repairRegistratorAddress(userEntity);
                progress = i * 100 / l;
                if (progress != lastProgress) {
                    lastProgress = progress;
                    repairRegistratorAddressesStatus.set(progress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void repairRegistratorAddress(UserEntity userEntity) {
        try {
            String registratorLevel = registratorDataService.getRegistratorLevel(userEntity.getId());
            if (!StringUtils.isBlank(registratorLevel)) {
                System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
                System.out.println("Данные системы:");
                SupportAddress sharerSupportAddress = supportAddressService.getRegistratorOfficeSupportAddress(userEntity);

                YandexGeocoderResponse response = YandexGeocoder.doGeocoderRequest(sharerSupportAddress.getJoinedAddressBase());
                System.out.println("Данные яндекса:");
                SupportAddress yandexSupportAddress = supportAddressService.getSupportAddressFromYandexGeocoderResponse(response);

                SupportAddress mergedSupportAddress = mergeAddresses(sharerSupportAddress, yandexSupportAddress);

                System.out.println("search          [" + sharerSupportAddress.getJoinedAddressBase() + "]");
                System.out.println("original        [" + sharerSupportAddress.getGeoPosition() + "] [" + sharerSupportAddress.getGeoLocation() + "]");
                System.out.println("joined original [" + sharerSupportAddress.getJoinedAddress() + "]");
                System.out.println("yandex          [" + yandexSupportAddress.getGeoPosition() + "] [" + yandexSupportAddress.getGeoLocation() + "]");
                System.out.println("joined yandex   [" + yandexSupportAddress.getJoinedAddress() + "]");
                System.out.println("result          [" + mergedSupportAddress.getJoinedAddress(true) + "]");
                System.out.println("location        [" + mergedSupportAddress.getFullAddress() + "]");

                System.out.println("---");

                if (mergedSupportAddress.isReliableByCoordinates()) {
                    System.out.println("Совпадение по координатам. Допускается коррекция в автоматическом режиме.");
                    supportAddressService.saveSharerRegistratorOfficeSupportAddress(userEntity, mergedSupportAddress);
                } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().isAllMatches() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                    System.out.println("Совпадение по полям. Допускается коррекция в автоматическом режиме.");
                    supportAddressService.saveSharerRegistratorOfficeSupportAddress(userEntity, mergedSupportAddress);
                } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                    System.out.println("Совпадение по части полей(норма). Автоматическая коррекция выполненна не будет.");
                } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() < PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                    System.out.println("Совпадение по части полей(ниже нормы). Автоматическая коррекция выполненна не будет.");
                } else {
                    System.out.println("Совпаденй не обнаружено. Автоматическая коррекция выполненна не будет.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Адрес объединения(фактический)
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private AtomicInteger repairCommunityActualAddressesStatus = new AtomicInteger(-1); // -1 остановленн, [0-100] статус выполнения в процентах

    public ProgressInfo getRepairCommunityActualAddressesStatus() {
        int status = repairCommunityActualAddressesStatus.get();
        String name = "repairCommunityActualAddressesStatus";

        ProgressInfo info;
        if (status != -1) {
            info = new ProgressInfo(ProgressStatus.RUNNING, status, name);
        } else {
            info = new ProgressInfo(ProgressStatus.STOPPED, 0, name);
        }
        return info;
    }

    @Async
    public void repairCommunityActualAddresses() {
        try {
            int status = repairCommunityActualAddressesStatus.get();
            if (status == -1) {
                repairCommunityActualAddressesStatus.set(0);
                repairCommunityActualAddressesTask();
                repairCommunityActualAddressesStatus.set(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            repairCommunityActualAddressesStatus.set(-1);
        }
    }

    private void repairCommunityActualAddressesTask() {
        try {
            int progress = 0;
            int lastProgress = progress;
            List<CommunityEntity> communities = communityDao.findAll();

            for (int i = 0, l = communities.size(); i < l; i++) {
                CommunityEntity community = communities.get(i);
                if(!community.isDeleted()) {
                    repairCommunityActualAddress(community);
                }
                progress = i * 100 / l;
                if (progress != lastProgress) {
                    lastProgress = progress;
                    repairCommunityActualAddressesStatus.set(progress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void repairCommunityActualAddress(CommunityEntity community) {
        try {
            System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

            System.out.println("Данные системы:");
            SupportAddress communitySupportAddress = supportAddressService.getCommunityActualSupportAddress(community);

            String addressBase = communitySupportAddress.getJoinedAddressBase();
            if(StringUtils.isBlank(addressBase)) {
                System.out.println("пропуск");
                return;
            }

            YandexGeocoderResponse response = YandexGeocoder.doGeocoderRequest(addressBase);
            System.out.println("Данные яндекса:");
            SupportAddress yandexSupportAddress = supportAddressService.getSupportAddressFromYandexGeocoderResponse(response);

            SupportAddress mergedSupportAddress = mergeAddresses(communitySupportAddress, yandexSupportAddress);

            System.out.println("search          [" + communitySupportAddress.getJoinedAddressBase() + "]");
            System.out.println("original        [" + communitySupportAddress.getGeoPosition() + "] [" + communitySupportAddress.getGeoLocation() + "]");
            System.out.println("joined original [" + communitySupportAddress.getJoinedAddress() + "]");
            System.out.println("yandex          [" + yandexSupportAddress.getGeoPosition() + "] [" + yandexSupportAddress.getGeoLocation() + "]");
            System.out.println("joined yandex   [" + yandexSupportAddress.getJoinedAddress() + "]");
            System.out.println("result          [" + mergedSupportAddress.getJoinedAddress(true) + "]");
            System.out.println("location        [" + mergedSupportAddress.getFullAddress() + "]");

            System.out.println("---");

            if (mergedSupportAddress.isReliableByCoordinates()) {
                System.out.println("Совпадение по координатам. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveCommunityActualSupportAddress(community, mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().isAllMatches() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по полям. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveCommunityActualSupportAddress(community, mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(норма). Автоматическая коррекция выполненна не будет.");
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() < PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(ниже нормы). Автоматическая коррекция выполненна не будет.");
            } else {
                System.out.println("Совпаденй не обнаружено. Автоматическая коррекция выполненна не будет.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Адрес объединения(регистрационный)
    //-----------------------------------------------------------------------------------------------------------------------------------------------
    private AtomicInteger repairCommunityRegistrationAddressesStatus = new AtomicInteger(-1); // -1 остановленн, [0-100] статус выполнения в процентах

    public ProgressInfo getRepairCommunityRegistrationAddressesStatus() {
        int status = repairCommunityRegistrationAddressesStatus.get();
        String name = "repairCommunityRegistrationAddressesStatus";

        ProgressInfo info;
        if (status != -1) {
            info = new ProgressInfo(ProgressStatus.RUNNING, status, name);
        } else {
            info = new ProgressInfo(ProgressStatus.STOPPED, 0, name);
        }
        return info;
    }

    @Async
    public void repairCommunityRegistrationAddresses() {
        try {
            int status = repairCommunityRegistrationAddressesStatus.get();
            if (status == -1) {
                repairCommunityRegistrationAddressesStatus.set(0);
                repairCommunityRegistrationAddressesTask();
                repairCommunityRegistrationAddressesStatus.set(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            repairCommunityRegistrationAddressesStatus.set(-1);
        }
    }

    private void repairCommunityRegistrationAddressesTask() {
        try {
            int progress = 0;
            int lastProgress = progress;
            List<CommunityEntity> communities = communityDao.findAll();

            for (int i = 0, l = communities.size(); i < l; i++) {
                CommunityEntity community = communities.get(i);
                if(!community.isDeleted()) {
                    repairCommunityRegistrationAddress(community);
                }
                progress = i * 100 / l;
                if (progress != lastProgress) {
                    lastProgress = progress;
                    repairCommunityRegistrationAddressesStatus.set(progress);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void repairCommunityRegistrationAddress(CommunityEntity community) {
        try {
            System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

            System.out.println("Данные системы:");
            SupportAddress communitySupportAddress = supportAddressService.getCommunityRegistrationSupportAddress(community);

            String addressBase = communitySupportAddress.getJoinedAddressBase();
            if(StringUtils.isBlank(addressBase)) {
                System.out.println("пропуск");
                return;
            }

            YandexGeocoderResponse response = YandexGeocoder.doGeocoderRequest(addressBase);
            System.out.println("Данные яндекса:");
            SupportAddress yandexSupportAddress = supportAddressService.getSupportAddressFromYandexGeocoderResponse(response);

            SupportAddress mergedSupportAddress = mergeAddresses(communitySupportAddress, yandexSupportAddress);

            System.out.println("search          [" + communitySupportAddress.getJoinedAddressBase() + "]");
            System.out.println("original        [" + communitySupportAddress.getGeoPosition() + "] [" + communitySupportAddress.getGeoLocation() + "]");
            System.out.println("joined original [" + communitySupportAddress.getJoinedAddress() + "]");
            System.out.println("yandex          [" + yandexSupportAddress.getGeoPosition() + "] [" + yandexSupportAddress.getGeoLocation() + "]");
            System.out.println("joined yandex   [" + yandexSupportAddress.getJoinedAddress() + "]");
            System.out.println("result          [" + mergedSupportAddress.getJoinedAddress(true) + "]");
            System.out.println("location        [" + mergedSupportAddress.getFullAddress() + "]");

            System.out.println("---");

            if (mergedSupportAddress.isReliableByCoordinates()) {
                System.out.println("Совпадение по координатам. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveCommunityRegistrationSupportAddress(community, mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().isAllMatches() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по полям. Допускается коррекция в автоматическом режиме.");
                supportAddressService.saveCommunityRegistrationSupportAddress(community, mergedSupportAddress);
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(норма). Автоматическая коррекция выполненна не будет.");
            } else if (mergedSupportAddress.isReliableBySimilarity() && mergedSupportAddress.getSimilarities().getMatchesCount() < PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                System.out.println("Совпадение по части полей(ниже нормы). Автоматическая коррекция выполненна не будет.");
            } else {
                System.out.println("Совпаденй не обнаружено. Автоматическая коррекция выполненна не будет.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------

    // Сравнивает "Площадь Ленина" и "Ленина Площадь"
    private boolean equalsNamesInverse(String name1, String name2) {
        String[] parts1 = name1.split(" ");
        String[] parts2 = name2.split(" ");
        return parts1.length == 2 && parts2.length == 2
                && StringUtils.equalsIgnoreCase(parts1[0], parts2[1]) && StringUtils.equalsIgnoreCase(parts1[1], parts2[0]);
    }

    private boolean equalsNames(String name1, String name2) {
        if (name1 == null || name2 == null) {
            return StringUtils.equalsIgnoreCase(name1, name2);
        }

        name1 = name1.trim().replaceAll("\\s+", " ");
        name2 = name2.trim().replaceAll("\\s+", " ");

        name1 = name1.replace("ё","е");
        name1 = name1.replace("Ё","Е");

        name2 = name2.replace("ё","е");
        name2 = name2.replace("Ё","Е");

        return StringUtils.equalsIgnoreCase(name1, name2) || equalsNamesInverse(name1, name2);
    }

    // Сравнивает geoPosition1 и geoPosition2. Формат "число, число".
    // Метод возвращает истину если в geoPosition1 и geoPosition2 числа равны, но следуют в разном порядке.
    // Этот метод нужен для того, чтобы выявлять координаты с неправильной последовательностью координат
    private boolean equalsGeoPositionsInverse(String geoPosition1, String geoPosition2) {
        String[] parts1 = geoPosition1.split(",");
        String[] parts2 = geoPosition2.split(",");
        return parts1.length == 2 && parts2.length == 2
                && StringUtils.equals(parts1[0], parts2[1]) && StringUtils.equals(parts1[1], parts2[0]);
    }

    // Возвращает количество схожих полей
    private Similarities isSimilarSupportAddresses(SupportAddress a, SupportAddress b) {
        int matchesCount = 0;
        boolean allMatches = true; // совпадают все определённые у a поля

        // Заданы ли в обоих случаях страны
        boolean isCountriesDefined = !StringUtils.isBlank(a.getCountry());
        // Равны ли они
        boolean isCountryEquals = StringUtils.equalsIgnoreCase(a.getCountry(), b.getCountry());
        if (isCountriesDefined && isCountryEquals) {
            // если они заданы и равны, то увеличиваем счётчик совпадений
            matchesCount++;
        } else if (isCountriesDefined && !isCountryEquals) {
            // если они заданы, но не равны, тогда выставляем признак полного равенства в false
            allMatches = false;
        }

        boolean isRegionsDefined = !StringUtils.isBlank(a.getRegion());
        boolean isRegionsEquals = StringUtils.equalsIgnoreCase(a.getRegion(), b.getRegion());
        if (isRegionsDefined && isRegionsEquals) {
            matchesCount++;
        } else if (isRegionsDefined && !isRegionsEquals) {
            allMatches = false;
        }

        boolean isAreasDefined = !StringUtils.isBlank(a.getArea());
        boolean isAreasEquals = StringUtils.equalsIgnoreCase(a.getArea(), b.getArea());
        if (isAreasDefined && isAreasEquals) {
            matchesCount++;
        } else if (isAreasDefined && !isAreasEquals) {
            allMatches = false;
        }

        boolean isCitiesDefined = !StringUtils.isBlank(a.getCity());
        boolean isCitiesEquals = StringUtils.equalsIgnoreCase(a.getCity(), b.getCity());
        if (isCitiesDefined && isCitiesEquals) {
            matchesCount++;
        } else if (isCitiesDefined && !isCitiesEquals) {
            allMatches = false;
        }

        boolean isStreetsDefined = !StringUtils.isBlank(a.getStreet());
        boolean isStreetsEquals = equalsNames(a.getStreet(), b.getStreet());
        if (isStreetsDefined && isStreetsEquals) {
            matchesCount++;
        } else if (isStreetsDefined && !isStreetsEquals) {
            allMatches = false;
        }

        boolean isHousesDefined = !StringUtils.isBlank(a.getHouse());
        boolean isHousesEquals = StringUtils.equalsIgnoreCase(a.getHouse(), b.getHouse());
        if (isHousesDefined && isHousesEquals) {
            matchesCount++;
        } else if (isHousesDefined && !isHousesEquals) {
            allMatches = false;
        }

        // Корпус и квартиру не проверяем, так как у Яндекса нет таких данных.
        // (Корпус у Яндекса есть, но он входит в состав дома)

        return new Similarities(matchesCount, allMatches);
    }

    /**
     * Возвращает не пустую строку из priority и other.
     * Если обе строки не пустые, то возвращает priority.
     * Если обе строки пустые то возвращает пустую строку.
     *
     * @param priority приоритетная строка
     * @param other    другая строка
     * @return
     */
    private String notEmptyString(String priority, String other) {
        String result = "";
        if (!StringUtils.isBlank(priority)) {
            result = priority;
        } else if (!StringUtils.isBlank(other)) {
            result = other;
        }
        return result;
    }

    private String getCorrectedDescription(String name, String description, String otherName, String otherDescription) {
        String result = "";

        name = StringUtils.trim(name);
        description = StringUtils.trim(description);

        otherName = StringUtils.trim(otherName);
        otherDescription = StringUtils.trim(otherDescription);

        // Например "улица Водопьяного" и "спуск Водопьяного"(правильный вариант), или name вообще нет
        if (equalsNames(name, otherName) || StringUtils.isBlank(name)) {
            // Если совпали имена ("Водопьяного"==="Водопьяного")

            if (!StringUtils.isBlank(otherDescription)) {
                // то в случае не пустого описания otherDescription ("спуск") выставляем его
                result = otherDescription;
            } else if (!StringUtils.isBlank(description)) {
                // иначе оставляем первоначальное описание description
                result = description;
            }
        } else {
            // иначе если имена не совпадают ("Водопьяного" != "Водой пьян я"), принимает данный вариант как разные результаты, и оставляем первоначальное описание description
            if (!StringUtils.isBlank(description)) {
                result = description;
            } else if(!StringUtils.isBlank(otherDescription)) {
                result = otherDescription;
            }
        }

        return result;
    }

    private SupportAddress merge(SupportAddress sharerAddress, SupportAddress yandexAddress) {
        SupportAddress mergedAddress = new SupportAddress();

        String country = ""; // страна

        String postalCode = ""; // почтовый индекс

        String region = ""; // регион
        String regionDescription = "";

        String area = ""; // район
        String areaDescription = "";

        String city = ""; // населённый пункт
        String cityDescription = "";

        String street = ""; // улица
        String streetDescription = "";

        String house = ""; // дом
        String houseDescription = "";

        String subHouse = ""; // корпус

        String room = ""; // квартира
        String roomDescription = "";

        String office = ""; // офис
        String officeDescription = "";

        String geoPosition = ""; // координаты
        String geoLocation = ""; // полный адрес

        // ===

        country = notEmptyString(yandexAddress.getCountry(), sharerAddress.getCountry());
        postalCode = notEmptyString(yandexAddress.getPostalCode(), sharerAddress.getPostalCode());

        region = notEmptyString(yandexAddress.getRegion(), sharerAddress.getRegion());
        regionDescription = getCorrectedDescription(sharerAddress.getRegion(), sharerAddress.getRegionDescription(), yandexAddress.getRegion(), yandexAddress.getRegionDescription());

        // TODO Если совпало по координатам и полям, то выставять вариант яндекса
        area = notEmptyString(yandexAddress.getArea(), sharerAddress.getArea());
        areaDescription = getCorrectedDescription(sharerAddress.getArea(), sharerAddress.getAreaDescription(), yandexAddress.getArea(), yandexAddress.getAreaDescription());

        city = notEmptyString(yandexAddress.getCity(), sharerAddress.getCity());
        cityDescription = getCorrectedDescription(sharerAddress.getCity(), sharerAddress.getCityDescription(), yandexAddress.getCity(), yandexAddress.getCityDescription());

        street = notEmptyString(yandexAddress.getStreet(), sharerAddress.getStreet());
        streetDescription = getCorrectedDescription(sharerAddress.getStreet(), sharerAddress.getStreetDescription(), yandexAddress.getStreet(), yandexAddress.getStreetDescription());

        // приоритет дома и корпуса у sharerAddress
        house = notEmptyString(sharerAddress.getHouse(), yandexAddress.getHouse());
        houseDescription = getCorrectedDescription(sharerAddress.getHouse(), sharerAddress.getHouseDescription(), yandexAddress.getHouse(), yandexAddress.getHouseDescription());

        // корпус пишем только есть есть дом, инче смысла нет
        if (!StringUtils.isBlank(house)) {
            subHouse = notEmptyString(sharerAddress.getSubHouse(), yandexAddress.getSubHouse());
        }

        room = notEmptyString(yandexAddress.getRoom(), sharerAddress.getRoom());
        roomDescription = getCorrectedDescription(sharerAddress.getRoom(), sharerAddress.getRoomDescription(), yandexAddress.getRoom(), yandexAddress.getRoomDescription());

        office = notEmptyString(yandexAddress.getOffice(), sharerAddress.getOffice());
        officeDescription = getCorrectedDescription(sharerAddress.getOffice(), sharerAddress.getOfficeDescription(), yandexAddress.getOffice(), yandexAddress.getOfficeDescription());

        geoLocation = notEmptyString(yandexAddress.getGeoLocation(), sharerAddress.getGeoLocation());
        geoPosition = yandexAddress.getGeoPosition();

        // ===

        mergedAddress.setCountry(country);
        mergedAddress.setPostalCode(postalCode);

        mergedAddress.setRegion(region);
        mergedAddress.setRegionDescription(regionDescription);

        mergedAddress.setArea(area);
        mergedAddress.setAreaDescription(areaDescription);

        mergedAddress.setCity(city);
        mergedAddress.setCityDescription(cityDescription);

        mergedAddress.setStreet(street);
        mergedAddress.setStreetDescription(streetDescription);

        mergedAddress.setHouse(house);
        mergedAddress.setHouseDescription(houseDescription);

        mergedAddress.setSubHouse(subHouse);

        mergedAddress.setRoom(room);
        mergedAddress.setRoomDescription(roomDescription);

        mergedAddress.setOffice(office);
        mergedAddress.setOfficeDescription(officeDescription);

        mergedAddress.setGeoPosition(geoPosition);
        mergedAddress.setGeoLocation(geoLocation);

        supportAddressService.processSupportAddress(mergedAddress);

        return mergedAddress;
    }

    // Пытается собрать более правильный адресс из того что уже есть в системе и адреса от Яндекса
    private SupportAddress mergeAddresses(SupportAddress sharerAddress, SupportAddress yandexAddress) {
        SupportAddress mergedAddress = new SupportAddress();

        boolean systemHadCoordinates = false; // в системе имеются координаты
        boolean isReliableByCoordinates = false; // одинаковые координаты
        boolean isReliableBySimilarity = false; // похожие адреса

        Similarities similarities = new Similarities(0, false);

        if (StringUtils.isBlank(sharerAddress.getGeoPosition())) {
            systemHadCoordinates = false;

            if (!StringUtils.isBlank(yandexAddress.getGeoPosition())) {
                // Если в системе координаты отсутствуют, а у Яндекса присутствуют.

                // Стоит учитывать, что координаты(как и сам адрес Яндекса) может быть неправильным.
                // Стоит учитывать, что в системе может быть адрес(как отдельный поля, так и поле с полным адресом),
                // но отсутствовали координаты.

                // Пытаемся сравнить страны, районы и тд из sharerAddress и yandexAddress.

                similarities = isSimilarSupportAddresses(sharerAddress, yandexAddress);
                if (similarities.isAllMatches()) {
                    // Если они похожи(все определённые поля), то можно достаточно точно утверждать, что речь идёт об одном и том же адресе,
                    // что даст право коррекции существующего адреса в сисеме данными Яндекса.

                    isReliableByCoordinates = false;
                    isReliableBySimilarity = true;

                    mergedAddress = merge(sharerAddress, yandexAddress);

                    // В таком случае так же следует делать пометку о том, что данные могут быть не точными.
                    // В таком случае следует выводить данные оператору для проверки и подтверждения.
                    // (внимательная проверка)
                } else if (similarities.getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                    // Если они похожи(количество совпавших полей больше определённого числа),
                    // то можно достаточно точно утверждать, что речь идёт об одном и том же адресе,
                    // что даст право коррекции существующего адреса в сисеме данными Яндекса.

                    isReliableByCoordinates = false;
                    isReliableBySimilarity = true;

                    mergedAddress = merge(sharerAddress, yandexAddress);

                    // В таком случае так же следует делать пометку о том, что данные могут быть не точными.
                    // В таком случае следует выводить данные оператору для проверки и подтверждения.
                    // (внимательная проверка)
                } else {
                    // Если же sharerAddress и yandexAddress не сравнимы, то НЕЛЬЯ утверждать,
                    // что в обоих случаях речь идёт об одном и том же адресе.

                    isReliableByCoordinates = false;
                    isReliableBySimilarity = false;

                    // не мержим адресс, т.к. он скорее всего неправильный

                    // В таком случае следует выводить данные оператору для коррекции.
                    // (полная коррекция опрератором, данные скорее всего неправильные)
                }
            } else {
                System.out.println("Не удалось определить адрес");

                isReliableByCoordinates = false;
                isReliableBySimilarity = false;

                // Если и в системе и у Яндекса координаты отсутствуют

                // увы, но координаты определить не удалось :(
                // В таком случае следует выводить данные оператору для коррекции.
                // (полная коррекция опрератором, данные скорее всего неправильные)
            }
        } else {
            systemHadCoordinates = true;

            // В системе долгота-широта может быть записана в обратном порядке.
            // Следующая проверка сравнивает координаты, учитывая, что в системе они могут быть записаны в неправильном порядке.
            boolean isGeoPositionsEquals = StringUtils.equals(sharerAddress.getGeoPosition(), yandexAddress.getGeoPosition());
            boolean isGeoPositionsEqualsInverse = equalsGeoPositionsInverse(sharerAddress.getGeoPosition(), yandexAddress.getGeoPosition());
            if (isGeoPositionsEquals || isGeoPositionsEqualsInverse) {
                // Так как координаты в системе, и координаты найденные через Яндекс совпадают,
                // то можно говорить, что в обоих случаях речь идёт об одном и том же адресе.
                // Нам это даёт возможность коррекции существующего адреса в сисеме данными Яндекса.

                // Совпали координаты
                isReliableByCoordinates = true;
                isReliableBySimilarity = false; //false т.к. проверку похожести не делаем

                mergedAddress = merge(sharerAddress, yandexAddress);

                // В таком случае следует выводить данные оператору для проверки и подтверждения.
                // (внимательная проверка)
            } else {
                // Так как координаты в системе, и координаты найденные через Яндекс НЕ совпадают,
                // то НЕЛЬЯ говорить, что в обоих случаях речь идёт об одном и том же адресе.
                // Это означает, что либо адрес который в системе неправильный, либо Яндекс адрес неправильный, либо всё неправильно.

                // В таком случае следует выводить данные оператору для коррекции.
                // (полная коррекция опрератором, данные скорее всего неправильные)

                // Случай со сравнимостью адресов пока не рассматриваю
                // Пытаемся сравнить страны, районы и тд из sharerAddress и yandexAddress.
                similarities = isSimilarSupportAddresses(sharerAddress, yandexAddress);
                if (similarities.isAllMatches()) {
                    // Если они похожи(все определённые поля), то можно достаточно точно утверждать, что речь идёт об одном и том же адресе,
                    // что даст право коррекции существующего адреса в сисеме данными Яндекса.

                    isReliableByCoordinates = false;
                    isReliableBySimilarity = true;

                    mergedAddress = merge(sharerAddress, yandexAddress);

                    // В таком случае так же следует делать пометку о том, что данные могут быть не точными.
                    // В таком случае следует выводить данные оператору для проверки и подтверждения.
                    // (внимательная проверка)
                } else if (similarities.getMatchesCount() >= PERMITTED_VALUE_FOR_SIMILAR_ADDRESSES) {
                    // Если они сравнимы, то можно полагать, что речь идёт об одном и том же адресе,
                    // а координаты в системе неправильные.

                    isReliableByCoordinates = false;
                    isReliableBySimilarity = true;

                    mergedAddress = merge(sharerAddress, yandexAddress);

                    // В таком случае следует выводить данные оператору для проверки, коррекции и подтверждения.
                    // (полная или частичная коррекция опрератором)
                } else {
                    // Если же sharerAddress и yandexAddress не сравнимы, то НЕЛЬЯ утверждать,
                    // что в обоих случаях речь идёт об одном и том же адресе.

                    isReliableByCoordinates = false;
                    isReliableBySimilarity = false;

                    // не мержим адресс, т.к. он скорее всего неправильный

                    // В таком случае следует выводить данные оператору для коррекции.
                    // (полная коррекция опрератором, данные скорее всего неправильные)
                }
            }
        }

        mergedAddress.setSystemHadCoordinates(systemHadCoordinates);
        mergedAddress.setReliableByCoordinates(isReliableByCoordinates);
        mergedAddress.setReliableBySimilarity(isReliableBySimilarity);
        mergedAddress.setSimilarities(similarities);

        supportAddressService.processSupportAddress(mergedAddress);
        return mergedAddress;
    }


}