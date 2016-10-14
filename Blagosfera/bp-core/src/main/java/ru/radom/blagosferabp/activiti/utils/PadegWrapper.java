package ru.radom.blagosferabp.activiti.utils;

import padeg.lib.EDeclenError;
import padeg.lib.Padeg;

/**
 * Created by Otts Alexey on 25.11.2015.<br/>
 * Обертка вокруг {@link Padeg}, чтобы можно было его использовать из BPM
 */
public class PadegWrapper {

    public String getFIOPadeg(String lastName, String firstName, String middleName, boolean sex, int padeg) throws EDeclenError {
        return Padeg.getFIOPadeg(lastName, firstName, middleName, sex, padeg);
    }

    public String getFIOPadegAS(String lastName, String firstName, String middleName, int padeg) throws EDeclenError {
        return Padeg.getFIOPadegAS(lastName, firstName, middleName, padeg);
    }

    public String getCutFIOPadeg(String lastName, String firstName, String middleName, boolean sex, int padeg) throws EDeclenError {
        return Padeg.getCutFIOPadeg(lastName, firstName, middleName, sex, padeg);
    }

    public String getFIOPadegFS(String fio, boolean sex, int padeg) throws EDeclenError {
        return Padeg.getFIOPadegFS(fio, sex, padeg);
    }

    public String getFIOPadegFSAS(String fio, int padeg) throws EDeclenError {
        return Padeg.getFIOPadegFSAS(fio, padeg);
    }

    public String getCutFIOPadegFS(String fio, boolean sex, int padeg) throws EDeclenError {
        return Padeg.getCutFIOPadegFS(fio, sex, padeg);
    }

    public String getIFPadeg(String firstName, String lastName, boolean sex, int padeg) throws EDeclenError {
        return Padeg.getIFPadeg(firstName, lastName, sex, padeg);
    }

    public String getIFPadegFS(String cIF, boolean sex, int padeg) throws EDeclenError {
        return Padeg.getIFPadegFS(cIF, sex, padeg);
    }

    public String getAppointmentPadeg(String s, int padeg) throws EDeclenError {
        return Padeg.getAppointmentPadeg(s, padeg);
    }

    public String getOfficePadeg(String s, int padeg) throws EDeclenError {
        return Padeg.getOfficePadeg(s, padeg);
    }

    public String getFullAppointmentPadeg(String appointment, String office, int padeg) throws EDeclenError {
        return Padeg.getFullAppointmentPadeg(appointment, office, padeg);
    }

    public int getSex(String middleName) {
        return Padeg.getSex(middleName);
    }
}
