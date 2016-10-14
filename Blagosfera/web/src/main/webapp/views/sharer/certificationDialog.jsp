<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
    #finger-select-block #hands {
        position: relative;
        height: 420px;
        background-image: url("/i/hands.png");
    }

    #finger-select-block #hands .finger-control {
        position: absolute;
        display: block;
        width: 40px;
        height: 40px;

        border: 1px solid;
        opacity: 0.7;

        cursor: pointer;

        -webkit-border-radius: 20px;
        -moz-border-radius: 20px;
        border-radius: 20px;

        -webkit-transition: all 0.3s linear 0s;
        -moz-transition: all 0.3s linear 0s;
        -o-transition: all 0.3s linear 0s;
        transition: all 0.3s linear 0s;
    }

    #finger-select-block #hands .finger-control.not-exists {
        color: #333;
        background-color: #f5f5f5;
        border-color: #ddd;
    }

    #finger-select-block #hands .finger-control.not-exists:hover {
        color: #31708f;
        background-color: #d9edf7;
        border-color: #bce8f1;
    }

    #finger-select-block #hands .finger-control.exists {
        color: #3c763d;
        background-color: #dff0d8;
        border-color: #d6e9c6;
    }

    #finger-select-block #hands .finger-control.exists:hover {
        color: #a94442;
        background-color: #f2dede;
        border-color: #ebccd1;
    }

    #finger-select-block #hands .finger-control .status-icon {
        position: absolute;
        width: 20px;
        height: 20px;
        line-height: 20px;
        text-align: center;
        top: 10px;
        left: 10px;
        display: block;
    }

    #finger-select-block #hands .finger-control .status-icon.hover-icon {
        display: none;
    }

    #finger-select-block #hands .finger-control:hover {
        opacity: 0.9;

        -webkit-transition: all 0.3s linear 0s;
        -moz-transition: all 0.3s linear 0s;
        -o-transition: all 0.3s linear 0s;
        transition: all 0.3s linear 0s;
    }

    #finger-select-block #hands .finger-control:hover .status-icon {
        display: none;
    }

    #finger-select-block #hands .finger-control:hover .status-icon.hover-icon {
        display: block;
    }

    #finger-select-block #hands .finger-control[data-finger-number='1'] {
        left: 65px;
        top: 140px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='2'] {
        left: 115px;
        top: 55px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='3'] {
        left: 145px;
        top: 20px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='4'] {
        left: 217px;
        top: 20px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='5'] {
        left: 335px;
        top: 118px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='6'] {
        left: 524px;
        top: 118px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='7'] {
        left: 640px;
        top: 20px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='8'] {
        left: 714px;
        top: 20px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='9'] {
        left: 743px;
        top: 55px;
    }

    #finger-select-block #hands .finger-control[data-finger-number='10'] {
        left: 792px;
        top: 140px;
    }
</style>

<script id="certification-scan-description-template" type="x-tmpl-mustache">
	<div class="row">
		<div class="col-xs-2">
			<img class="img-thumbnail" src="{{avatar}}" />
		</div>
		<div class="col-xs-10">
			{{{text}}}
		</div>
	</div>
</script>

<script id="certification-scan-finger-template" type="x-tmpl-mustache">
	<div class="finger-control{{#exists}} exists{{/exists}}{{^exists}} not-exists{{/exists}}" data-finger-number="{{number}}" data-exists="{{exists}}">
		{{#exists}}
			<div class="status-icon">
				<i class="glyphicon glyphicon-ok"></i>
			</div>
			<div class="status-icon hover-icon">
				<i class="glyphicon glyphicon-remove"></i>
			</div>
		{{/exists}}
		
		{{^exists}}
			<div class="status-icon">
				<i class="glyphicon glyphicon-question-sign"></i>
			</div>
			<div class="status-icon hover-icon">
				<i class="glyphicon glyphicon-hand-up"></i>
			</div>		
		{{/exists}}
	</div>
</script>

<div class="modal fade" id="sharer-certification-dialog" tabindex="-1" role="dialog" aria-hidden="true"
     data-keyboard="false" data-backdrop="static">
    <div class="modal-dialog modal-xl modal-full-height">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Сертификация участника. До окончания осталось: <span id="sessionTimer"></span>
                </h4>
            </div>
            <div class="modal-body">

                <div class="panel panel-info" id="agreement-upload-panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Загрузка отсканированных документов
                            <i class="glyphicon glyphicon-ok-sign pull-right"></i>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <div class="alert alert-info" style="position: relative;">
                            На данном этапе необходимо выбрать файлы с отсканированными документами или их фотографии для загрузки на сервер
                            (<b>отсканированные страницы паспорта с персональными данными и адресом регистрации, а также фотографию пользователя
                            с открытым паспортом перед собой</b>).
                            Допустимые форматы: bmp, png, gif, jpg, jpeg, pdf.
                            <br/>
                            Для выбора каждого конкретного файла перетащите файл в область для загрузки файлов под этим сообщением,
                            либо кликните по самой области для загрузки файлов.
                            <br/>
                            После выбора всех необходимых файлов, используйте кнопку Загрузить, чтобы продолжить.
                        </div>
                    </div>
                    <div class="panel-footer text-right">
                        <a href="#" class="btn btn-info btn-xs" id="do-agreement-upload-link"><i
                                class="glyphicon glyphicon-ok"></i> Загрузить</a>
                    </div>
                </div>

                <div class="panel panel-info" id="fingers-panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Формирование образцов отпечатков пальцев
                            <i class="glyphicon glyphicon-ok-sign pull-right"></i>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group" id="finger-select-block">
                            <div id="hands"></div>
                        </div>
                        <div class="alert alert-info" id="message-alert" style="position : relative;"></div>
                        <div class="form-group" id="server-select-block">
                            <label>Выбор сервера авторизации</label>
                            <div class="row">
                                <div class="col-xs-6">
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="remote-server-address-input"
                                               placeholder="Адрес сервера"/>
										<span class="input-group-btn">
											<a class="btn btn-info" href="#" id="connect-to-remote-server-link"><i
                                                    class="fa fa-plug"></i> Подключиться</a>
			      						</span>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <a href="#" class="btn btn-default btn-block" id="connect-to-local-server-link"><i
                                            class="fa fa-plug"></i> Подключиться к локальному серверу авторизации</a>
                                </div>
                            </div>
                        </div>

                        <div class="form-group" id="scan-block">
                            <div class="alert" id="progressDiv"></div>
                            <div class="progress" id="scan-timeout"></div>
                        </div>

                    </div>
                    <div class="panel-footer text-right">
                        <a href="#" class="btn btn-warning btn-xs" id="cancel-fingers-link"><i
                                class="glyphicon glyphicon-remove"></i> Прервать</a>
                        <a href="#" class="btn btn-info btn-xs" id="retry-fingers-link"><i
                                class="glyphicon glyphicon-refresh"></i> Повторить</a>
                        <a href="#" class="btn btn-info btn-xs" id="continue-fingers-link"><i
                                class="glyphicon glyphicon-play"></i> Продолжить</a>
                        <a href="#" class="btn btn-success btn-xs" id="finish-fingers-link"><i
                                class="glyphicon glyphicon-ok"></i> Завершить сканирование пальцев</a>
                    </div>
                </div>

                <div class="panel panel-info" id="card-panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Запись карты
                            <i class="glyphicon glyphicon-ok-sign pull-right"></i>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <div class="alert alert-info" style="position : relative;"></div>
                    </div>
                    <div class="panel-footer text-right">
                        <a href="#" class="btn btn-info btn-xs" id="start-card-link"><i
                                class="glyphicon glyphicon-play"></i> Начать запись</a>
                        <a href="#" class="btn btn-info btn-xs" id="retry-card-link"><i
                                class="glyphicon glyphicon-refresh"></i> Повторить</a>
                        <a href="#" class="btn btn-warning btn-xs" id="cancel-card-link"><i
                                class="glyphicon glyphicon-remove"></i> Прервать</a>
                        <a href="#" class="btn btn-success btn-xs" id="finish-card-link"><i
                                class="glyphicon glyphicon-ok"></i> Завершить</a>
                        <a href="#" class="btn btn-info btn-xs" id="skip-card-link"><i
                                class="glyphicon glyphicon-play"></i> Пропустить</a>
                    </div>
                </div>

                <div class="panel panel-info" id="agreement-text-panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Подтверждение цифровой подписью Пользовательского соглашения
                            <i class="glyphicon glyphicon-ok-sign pull-right"></i>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <div class="alert alert-info">
                            Для идентификации профиля участнику необходимо принять приведенное ниже соглашение.
                            Чтобы открыть версию соглашения для печати в новой вкладке, используйте кнопку На печать.
                            После того как соглашение будет принято участником, используйте кнопку Далее
                            для продолжения процедуры.
                        </div>
                    </div>
                    <div class="panel-footer">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" id="agree-with-agreement">принять условия <a href="#" id="show-agreement-link">Пользовательского соглашения</a>
                                    </label>
                                </div>
                            </div>
                            <div class="col-md-6 text-right">
                                <a href="/certification-agreement-print" class="btn btn-info btn-xs"
                                   id="agreement-print-link" target="_blank"><i class="glyphicon glyphicon-print"></i>На
                                    печать</a>
                                <button class="btn btn-success btn-xs" id="go-to-agreement-upload" disabled="true"><i
                                        class="glyphicon glyphicon-ok"></i>Далее
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel panel-info" id="finish-panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            Завершение идентификации
                            <i class="glyphicon glyphicon-ok-sign pull-right"></i>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <div class="alert alert-info" id="ecp-alert">
                                Генерация не квалифицированной ЭЦП <i style="font-size : 22px;"
                                                                      class="pull-right fa fa-spinner faa-spin animated"></i>
                            </div>
                            <div class="alert alert-warning" id="certificate-alert">
                                Генерация сертификата участника системы <i style="font-size : 22px;"
                                                                           class="pull-right fa fa-clock-o"></i>
                            </div>
                            <div class="alert alert-warning" id="protect-alert">
                                Установка защиты на профиль пользователя <i style="font-size : 22px;"
                                                                            class="pull-right fa fa-clock-o"></i>
                            </div>
                            <div class="alert alert-warning" id="fixation-alert">
                                Фиксация изменений <i style="font-size : 22px;" class="pull-right fa fa-clock-o"></i>
                            </div>
                        </div>
                    </div>
                    <!--div class="panel-footer text-right">
                        <a href="#" class="btn btn-info btn-xs"><i class="glyphicon glyphicon-ok"></i> Завершить сертификацию</a>
                    </div-->
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" id="finish-button" data-dismiss="modal"><i
                        class="glyphicon glyphicon-ok"></i> Завершить
                </button>
                <button type="button" class="btn btn-warning" id="close-button"><i
                        class="glyphicon glyphicon-remove"></i> Закрыть
                </button>
            </div>
        </div>
    </div>
</div>

<div id="agreement-modal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Соглашение по идентификации физического лица</h4>
            </div>
            <div class="modal-body" id="agreement-text">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-dismiss" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>