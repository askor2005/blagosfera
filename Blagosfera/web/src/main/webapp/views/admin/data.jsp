<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<h1>
    Проверка и исправление данных в системе
</h1>

<hr/>

<p>ВНИМАНИЕ! Выполнение следующих процессов может вызвать замедление работы системы!</p>

<div id="repair-geo-positions-block">
    <hr/>

    <p>Исправление для координат офисов регистраторов в автоматическом режиме. Запустить следует всего один раз.</p>

    <p>В дальнейшем эта функция уберётся в пользу более продвинутых методов коррекции координат и адресов.</p>

    <button id="start-repair-geo-positions" class="btn btn-primary btn-sm">Начать</button>

    <br/>
    <br/>

    <div class="progress">
        <div id="repair-geo-positions-progress" class="progress-bar progress-bar-striped active" role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
        </div>
    </div>
</div>

<div id="repair-actual-addresses-block">
    <hr/>

    <p>Проверка и исправление <b>фактических адресов идентифицированных участников</b> в системе в автоматическом режиме.
        Запустить следует всего одина раз.</p>

    <p>Исправляет неправильные(обычно это заключается в порядке [широта,долгота]) координаты
        <br>Выставляет координаты если их не было
        <br>Выставляет регионы и районы
        <br>Убирает описания("улица", "ул", "город и тд") из полей где должно содержаться только название
        <br>Выставляет правильные описания(Район, Облать, Край, Улица, Бульвар и тд)</p>

    <button id="start-repair-actual-addresses" class="btn btn-primary btn-sm">Начать</button>

    <br/>
    <br/>

    <div class="progress">
        <div id="repair-actual-addresses-progress" class="progress-bar progress-bar-striped active" role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
        </div>
    </div>
</div>

<div id="repair-registration-addresses-block">
    <hr/>

    <p>Проверка и исправление <b>регистрационных адресов идентифицированных участников</b> в системе в автоматическом режиме.
        Запустить следует всего одина раз.</p>

    <p>Исправляет неправильные(обычно это заключается в порядке [широта,долгота]) координаты
        <br>Выставляет координаты если их не было
        <br>Выставляет регионы и районы
        <br>Убирает описания("улица", "ул", "город и тд") из полей где должно содержаться только название
        <br>Выставляет правильные описания(Район, Облать, Край, Улица, Бульвар и тд)</p>

    <button id="start-repair-registration-addresses" class="btn btn-primary btn-sm">Начать</button>

    <br/>
    <br/>

    <div class="progress">
        <div id="repair-registration-addresses-progress" class="progress-bar progress-bar-striped active"
             role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
        </div>
    </div>
</div>

<div id="repair-registrator-addresses-block">
    <hr/>

    <p>Проверка и исправление <b>адресов офисов регистраторов</b> в системе в автоматическом режиме.
        Запустить следует всего одина раз.</p>

    <p>Исправляет неправильные(обычно это заключается в порядке [широта,долгота]) координаты
        <br>Выставляет координаты если их не было
        <br>Выставляет регионы и районы
        <br>Убирает описания("улица", "ул", "город и тд") из полей где должно содержаться только название
        <br>Выставляет правильные описания(Район, Облать, Край, Улица, Бульвар и тд)</p>

    <button id="start-repair-registrator-addresses" class="btn btn-primary btn-sm">Начать</button>

    <br/>
    <br/>

    <div class="progress">
        <div id="repair-registrator-addresses-progress" class="progress-bar progress-bar-striped active"
             role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
        </div>
    </div>
</div>

<div id="repair-community-actual-addresses-block">
    <hr/>

    <p>Проверка и исправление <b>фактических адресов объединений</b> в системе в автоматическом режиме.
        Запустить следует всего одина раз.</p>

    <p>Исправляет неправильные(обычно это заключается в порядке [широта,долгота]) координаты
        <br>Выставляет координаты если их не было
        <br>Выставляет регионы и районы
        <br>Убирает описания("улица", "ул", "город и тд") из полей где должно содержаться только название
        <br>Выставляет правильные описания(Район, Облать, Край, Улица, Бульвар и тд)</p>

    <button id="start-repair-community-actual-addresses" class="btn btn-primary btn-sm">Начать</button>

    <br/>
    <br/>

    <div class="progress">
        <div id="repair-community-actual-addresses-progress" class="progress-bar progress-bar-striped active" role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
        </div>
    </div>
</div>

<div id="repair-community-registration-addresses-block">
    <hr/>

    <p>Проверка и исправление <b>регистрационных адресов объединений</b> в системе в автоматическом режиме.
        Запустить следует всего одина раз.</p>

    <p>Исправляет неправильные(обычно это заключается в порядке [широта,долгота]) координаты
        <br>Выставляет координаты если их не было
        <br>Выставляет регионы и районы
        <br>Убирает описания("улица", "ул", "город и тд") из полей где должно содержаться только название
        <br>Выставляет правильные описания(Район, Облать, Край, Улица, Бульвар и тд)</p>

    <button id="start-repair-community-registration-addresses" class="btn btn-primary btn-sm">Начать</button>

    <br/>
    <br/>

    <div class="progress">
        <div id="repair-community-registration-addresses-progress" class="progress-bar progress-bar-striped active" role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:100%">
        </div>
    </div>
</div>

<script type="text/javascript">
    function initRepairGeoPositionsBlock() {
        var $repairStatus = $("#repair-geo-positions-progress");

        function updateProgress(progressInfo) {
            console.log("updateProgress: " + progressInfo);
            if (progressInfo.progressStatus === "RUNNING") {
                if (progressInfo.progressPercent >= 0) {
                    $repairStatus.css("width", progressInfo.progressPercent + "%");
                    $repairStatus.html(progressInfo.progressPercent + "%");
                }
            } else {
                $repairStatus.css("width", "100%");
                $repairStatus.html("");
            }
        }

        function updateStatus() {
            console.log("updateStatus");
            $.radomJsonPost("/admin/data/repairGeoPositionsStatus.json", {}, function (progressInfo) {
                updateProgress(progressInfo);
                if (progressInfo.progressStatus === "STOPPED") {
                    stopUpdate();
                } else {
                    startUpdate();
                }
            });
        }

        var interval;

        function startUpdate() {
            console.log("startUpdate");
            if (!interval) {
                interval = setInterval(updateStatus, 1000);
                updateProgress({progressStatus: "RUNNING", progressPercent: 0});
            }
        }

        function stopUpdate() {
            console.log("stopUpdate");
            clearInterval(interval);
            interval = null;
        }

        $("#start-repair-geo-positions").click(function () {
            $.radomJsonPost("/admin/data/repairGeoPositions.json", {}, function () {
                startUpdate();
            });
        });

        updateStatus();
    }

    function initRepairActualAddressesBlock() {
        var $repairStatus = $("#repair-actual-addresses-progress");

        function updateProgress(progressInfo) {
            console.log("updateProgress: " + progressInfo);
            if (progressInfo.progressStatus === "RUNNING") {
                if (progressInfo.progressPercent >= 0) {
                    $repairStatus.css("width", progressInfo.progressPercent + "%");
                    $repairStatus.html(progressInfo.progressPercent + "%");
                }
            } else {
                $repairStatus.css("width", "100%");
                $repairStatus.html("");
            }
        }

        function updateStatus() {
            console.log("updateStatus");
            $.radomJsonPost("/admin/data/repairActualAddressesStatus.json", {}, function (progressInfo) {
                updateProgress(progressInfo);
                if (progressInfo.progressStatus === "STOPPED") {
                    stopUpdate();
                } else {
                    startUpdate();
                }
            });
        }

        var interval;

        function startUpdate() {
            console.log("startUpdate");
            if (!interval) {
                interval = setInterval(updateStatus, 1000);
                updateProgress({progressStatus: "RUNNING", progressPercent: 0});
            }
        }

        function stopUpdate() {
            console.log("stopUpdate");
            clearInterval(interval);
            interval = null;
        }

        $("#start-repair-actual-addresses").click(function () {
            $.radomJsonPost("/admin/data/repairActualAddresses.json", {}, function () {
                startUpdate();
            });
        });

        updateStatus();
    }

    function initRepairRegistrationAddressesBlock() {
        var $repairStatus = $("#repair-registration-addresses-progress");

        function updateProgress(progressInfo) {
            console.log("updateProgress: " + progressInfo);
            if (progressInfo.progressStatus === "RUNNING") {
                if (progressInfo.progressPercent >= 0) {
                    $repairStatus.css("width", progressInfo.progressPercent + "%");
                    $repairStatus.html(progressInfo.progressPercent + "%");
                }
            } else {
                $repairStatus.css("width", "100%");
                $repairStatus.html("");
            }
        }

        function updateStatus() {
            console.log("updateStatus");
            $.radomJsonPost("/admin/data/repairRegistrationAddressesStatus.json", {}, function (progressInfo) {
                updateProgress(progressInfo);
                if (progressInfo.progressStatus === "STOPPED") {
                    stopUpdate();
                } else {
                    startUpdate();
                }
            });
        }

        var interval;

        function startUpdate() {
            console.log("startUpdate");
            if (!interval) {
                interval = setInterval(updateStatus, 1000);
                updateProgress({progressStatus: "RUNNING", progressPercent: 0});
            }
        }

        function stopUpdate() {
            console.log("stopUpdate");
            clearInterval(interval);
            interval = null;
        }

        $("#start-repair-registration-addresses").click(function () {
            $.radomJsonPost("/admin/data/repairRegistrationAddresses.json", {}, function () {
                startUpdate();
            });
        });

        updateStatus();
    }

    function initRepairRegistratorAddressesBlock() {
        var $repairStatus = $("#repair-registrator-addresses-progress");

        function updateProgress(progressInfo) {
            console.log("updateProgress: " + progressInfo);
            if (progressInfo.progressStatus === "RUNNING") {
                if (progressInfo.progressPercent >= 0) {
                    $repairStatus.css("width", progressInfo.progressPercent + "%");
                    $repairStatus.html(progressInfo.progressPercent + "%");
                }
            } else {
                $repairStatus.css("width", "100%");
                $repairStatus.html("");
            }
        }

        function updateStatus() {
            console.log("updateStatus");
            $.radomJsonPost("/admin/data/repairRegistratorAddressesStatus.json", {}, function (progressInfo) {
                updateProgress(progressInfo);
                if (progressInfo.progressStatus === "STOPPED") {
                    stopUpdate();
                } else {
                    startUpdate();
                }
            });
        }

        var interval;

        function startUpdate() {
            console.log("startUpdate");
            if (!interval) {
                interval = setInterval(updateStatus, 1000);
                updateProgress({progressStatus: "RUNNING", progressPercent: 0});
            }
        }

        function stopUpdate() {
            console.log("stopUpdate");
            clearInterval(interval);
            interval = null;
        }

        $("#start-repair-registrator-addresses").click(function () {
            $.radomJsonPost("/admin/data/repairRegistratorAddresses.json", {}, function () {
                startUpdate();
            });
        });

        updateStatus();
    }

    function initRepairCommunityActualAddressesBlock() {
        var $repairStatus = $("#repair-community-actual-addresses-progress");

        function updateProgress(progressInfo) {
            console.log("updateProgress: " + progressInfo);
            if (progressInfo.progressStatus === "RUNNING") {
                if (progressInfo.progressPercent >= 0) {
                    $repairStatus.css("width", progressInfo.progressPercent + "%");
                    $repairStatus.html(progressInfo.progressPercent + "%");
                }
            } else {
                $repairStatus.css("width", "100%");
                $repairStatus.html("");
            }
        }

        function updateStatus() {
            console.log("updateStatus");
            $.radomJsonPost("/admin/data/repairCommunityActualAddressesStatus.json", {}, function (progressInfo) {
                updateProgress(progressInfo);
                if (progressInfo.progressStatus === "STOPPED") {
                    stopUpdate();
                } else {
                    startUpdate();
                }
            });
        }

        var interval;

        function startUpdate() {
            console.log("startUpdate");
            if (!interval) {
                interval = setInterval(updateStatus, 1000);
                updateProgress({progressStatus: "RUNNING", progressPercent: 0});
            }
        }

        function stopUpdate() {
            console.log("stopUpdate");
            clearInterval(interval);
            interval = null;
        }

        $("#start-repair-community-actual-addresses").click(function () {
            $.radomJsonPost("/admin/data/repairCommunityActualAddresses.json", {}, function () {
                startUpdate();
            });
        });

        updateStatus();
    }

    function initRepairCommunityRegistrationAddressesBlock() {
        var $repairStatus = $("#repair-community-registration-addresses-progress");

        function updateProgress(progressInfo) {
            console.log("updateProgress: " + progressInfo);
            if (progressInfo.progressStatus === "RUNNING") {
                if (progressInfo.progressPercent >= 0) {
                    $repairStatus.css("width", progressInfo.progressPercent + "%");
                    $repairStatus.html(progressInfo.progressPercent + "%");
                }
            } else {
                $repairStatus.css("width", "100%");
                $repairStatus.html("");
            }
        }

        function updateStatus() {
            console.log("updateStatus");
            $.radomJsonPost("/admin/data/repairCommunityRegistrationAddressesStatus.json", {}, function (progressInfo) {
                updateProgress(progressInfo);
                if (progressInfo.progressStatus === "STOPPED") {
                    stopUpdate();
                } else {
                    startUpdate();
                }
            });
        }

        var interval;

        function startUpdate() {
            console.log("startUpdate");
            if (!interval) {
                interval = setInterval(updateStatus, 1000);
                updateProgress({progressStatus: "RUNNING", progressPercent: 0});
            }
        }

        function stopUpdate() {
            console.log("stopUpdate");
            clearInterval(interval);
            interval = null;
        }

        $("#start-repair-community-registration-addresses").click(function () {
            $.radomJsonPost("/admin/data/repairCommunityRegistrationAddresses.json", {}, function () {
                startUpdate();
            });
        });

        updateStatus();
    }


    $(document).ready(function () {
        initRepairGeoPositionsBlock();

        initRepairActualAddressesBlock();
        initRepairRegistrationAddressesBlock();
        initRepairRegistratorAddressesBlock();

        initRepairCommunityActualAddressesBlock();
        initRepairCommunityRegistrationAddressesBlock();
    });
</script>

<hr/>


