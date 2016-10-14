<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<style>
    .largeWidth .modal-dialog {
        width: 50% !important;
    }
</style>

<script type="text/javascript">
    $(document).ready(function() {
        var questionData;

        getQuestion();
        getCountsRecordsAndScore();

        $("#question-btn").click(function() {
            questionData.answer = $('#question-input').val();
            $('#question-input').val("");

            if (questionData.answer !== "") {
                $.ajax({
                    type: "post",
                    dataType: "json",
                    data: JSON.stringify(questionData),
                    url: '/cyberbrain/sections/answerTheQuestionByPriority',
                    success: function (response) {
                        if (response.result == "error") {
                            bootbox.alert(response.message);
                            $('#question-panel').css({display: "none"});
                        } else {
                            getQuestion();
                            getCountsRecordsAndScore();

                            if (typeof storeQuestions !== 'undefined') {
                                storeQuestions.load();
                            }

                            if (typeof storeQuestionsMany !== 'undefined') {
                                storeQuestionsMany.load();
                            }

                            if (typeof storeQuestionsProperties !== 'undefined') {
                                storeQuestionsProperties.load();
                            }
                        }
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            } else {
                bootbox.alert("Введите ответ на вопрос!");
            }
        });

        function getQuestion() {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{}",
                url: '/cyberbrain/sections/get_question_by_priority.json',
                success: function (response) {
                    if (response.result == "error") {
                        bootbox.alert(response.message);
                    } else {
                        if (JSON.stringify(response) !== "{}") {
                            questionData = response;
                            $('#question-panel').css({display: "block"});
                            $('#question-title').html(response.description + ' <button type="button" class="btn btn-link" id="question-link" style="max-width: 600px; word-wrap: break-word;height: 18px; margin: 0px; border-spacing: 0px;border: none; padding: 0px; position: relative; top: -2px;">' + response.tag + '</button>' + ' - это ?');

                            var tag = response.tag;
                            $('#question-link').click(function () {
                                $.ajax({
                                    type: "post",
                                    dataType: "json",
                                    data: "tag=" + response.tag,
                                    url: '/cyberbrain/sections/get_question_by_priority_info.json',
                                    success: function (response) {
                                        if (response.result === "") {
                                            response.result = "По данному тегу нет данных.";
                                        }

                                        bootbox.dialog({
                                            className: "largeWidth",
                                            message: response.result,
                                            title: 'Информация по тегу "' + tag + '"',
                                            buttons: {
                                                main: {
                                                    label: "Закрыть",
                                                    className: "btn-primary"
                                                }
                                            }
                                        });
                                    },
                                    error: function () {
                                        console.log("ajax error");
                                    }
                                });
                            });
                        } else {
                            $('#question-panel').css({display: "none"});
                        }
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }
    });

    /**
    * Глобальная функция для получения значений счетчиков для разделов и текущего кол-ва баллов у пользователя
     */
    function getCountsRecordsAndScore() {
        $.ajax({
            type : "post",
            dataType : "json",
            data: "{}",
            url : '/cyberbrain/sections/get_counts_records_and_score.json',
            success : function(response) {
                if (response.result == "error") {
                    bootbox.alert(response.message);
                } else {
                    if (JSON.stringify(response) !== "{}") {
                        $('#spanTaskManagement').html(response.taskManagementCount);
                        $('#spanJournalAttention').html(response.journalAttentionCount);
                        $('#spanThesaurus').html(response.thesaurusCount);
                        $('#spanKowledgeRepository').html(response.knowledgeRepositoryCount);
                        $('#spanUserScore').html(response.userScore);
                    }
                }
            },
            error : function() {
                console.log("ajax error");
            }
        });
    }
</script>

<h1>Разделы кибер мозга</h1>

<ul class="nav nav-pills" role="tablist" style="overflow:auto;">
    <li role="presentation"><a href="taskManagement">Цели и дела<span class="badge" id="spanTaskManagement"></span></a></li>
    <li role="presentation"><a href="journalAttention">Мое внимание<span class="badge" id="spanJournalAttention"></span></a></li>
    <li role="presentation"><a href="thesaurus">Мои термины<span class="badge" id="spanThesaurus"></span></a></li>
    <li role="presentation"><a href="knowledgeRepository">Вопросы<span class="badge" id="spanKowledgeRepository"></span></a></li>
    <li role="presentation"><a href="myKnowledge">Мои знания</a></li>
    <li role="presentation"><a href="ratingSystem">Рейтинги системы</a></li>
    <li role="presentation"><a href="importExport">Импорт / Экспорт данных</a></li>
    <li role="presentation" class="disabled" style="float:right;">
        <div class="well well-sm">Текущее кол-во баллов <span class="badge" id="spanUserScore">${userScore}</span></div>
    </li>
</ul>

<div class="alert alert-warning" role="alert" id="question-panel" style="display: none">
    <div class="input-group">
        <span class="input-group-addon" id="question-title" style="border: none; background: transparent"></span>
        <input type="text" class="form-control input-sm" id="question-input" placeholder="Введите здесь ответ на вопрос">
        <span class="input-group-btn">
            <button class="btn btn-default btn-sm" type="button" id="question-btn">Запомни</button>
        </span>
    </div>
</div>