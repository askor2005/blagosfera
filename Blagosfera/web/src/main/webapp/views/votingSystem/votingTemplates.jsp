<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<style>
    pre {
        font-family: helvetica,arial,verdana,sans-serif !important;
        word-break: normal !important;
        white-space: pre-line !important;
    }

    .votingTable tr[is_abstain=true] {
        background-color: #ccc;
    }
    .votingTable tr[is_abstain=true] td {
        background-color: #ccc;
        font-weight: bold;
    }

    .allreadyVotedTable th {
        border-bottom: none !important;
    }

    #votingForNonAbstainItemsText {

    }
    #votingForAbstainItemsText {
        display: none;
    }


    .votingItem {
        padding: 5px;
        border: 1px solid #ccc;
        margin-bottom: 5px;
        overflow: hidden;
        width: 100%;
    }
    .winnerVotingItem {
        background-color: #CCFFFF !important;
        border: 5px solid #ccc;
    }
    .winnerVotingItem .protocolItemContent {
        padding: 15px;
        border: 1px solid #ccc;
        width: 100%;
        min-height: 111px;
        color: #FF0000;
        font-size: 32px;
        background-color: #FFFFFF;
        text-align: center;
    }
    .lostVotingItem {
        background-color: #FFFFCC;
    }
    .lostVotingItem .protocolItemContent {
        padding: 5px;
        border: 1px solid #ccc;
        width: 100%;
        min-height: 70px;
        font-size: 25px;
        background-color: #FFFFFF;
        text-align: center;
    }


    .candidateItem {
        float: left;
    }
    .votingResultBlock {
        overflow: hidden;
        padding-left: 5px;
    }

    .verticalAlignSpan {
        display: inline-block;
        height: 100%;
        vertical-align: middle;
    }
    .voterBlock {
        display: inline-block; vertical-align: top; padding: 5px 5px 5px 5px;
    }
    .voterBlockIsVote {
        border: 2px solid green;
    }
    .voterBlockIsNotVote {
        border: 2px solid #ddd;
    }
    .votingProtocol {
        margin-top: 10px;
        display: none;
    }
    .emptyProtocol, .closedVotingProtocol {
        border: 1px solid #ccc; padding: 20px; font-size: 30px; color: #999999; text-align: center;
    }
    #descriptionPre {
        font-size: 16px;
        font-weight: bold;
    }
    #descriptionBlock {
        overflow: hidden;

    }
</style>
<script id="voteEndedDialogHeaderTemplate" type="x-tmpl-mustache">
    Голосование {{votingName}} завершено.</a>
</script>
<script id="voteEndedDialogContentTemplate" type="x-tmpl-mustache">
    Голосование завершено. Вы можете просмотреть результаты голосования на текущей странице. Чтобы перейти к следующему голосованию нажмите ссылку <a href="{{nextVotingLink}}">Перейти к следующему голосованию.</a>
</script>
<script id="votingBatchTemplate" type="x-tmpl-mustache">
    <h3><b>Тема:</b> {{response.subject}}</h3>
    <hr/>
    <button type="button" class="btn btn-primary" onclick="$('#meetingTargets').modal('show');" >${batchVotingDescription}</button>
    <c:if test="${dialog != null}" >
        <button type="button" class="btn btn-primary" id="batchVotingDialog" dialog_id="${dialog.id}" dialog_name="${dialog.name}">Чат собрания</button>
    </c:if>
    <br/><br/>
    <ul>
        {{#response.votings}}
            {{#visible}}
                <li id="voting_{{id}}"><a style="{{#isActive}}font-weight: bold;{{/isActive}} {{#isFinished}}color: green;{{/isFinished}}" href="{{votingLink}}">{{subject}}</a></li>
            {{/visible}}
        {{/response.votings}}
    </ul>
</script>

<script id="votingProtocolTemplate" type="x-tmpl-mustache">
    {{#errorCode}}
        <div>Произошла ошибка. Текст ошибки: {{message}}</div>
    {{/errorCode}}
    {{^errorCode}}
        <h3 id="voting-caption">Результаты голосования по вопросу:</h3>
        <div id="descriptionBlock">
            <pre id="descriptionPre">{{{response.additionalData.description}}}</pre>
        </div>
        <div class="form-group shadow" id="descriptionShadow"></div>
        <div class="form-group text-right">
            <a href="#" id="descriptionShow" class="btn btn-xs btn-default">Развернуть</a>
        </div>
        {{#response.stateVotingText}}
            <div id="restartBatchVotingBlock">
                <div>{{{response.stateVotingText}}}</div>
                {{#response.isCanRestartVoting}}
                    <div style="float: right;">
                        <button id="restartVoting" type="button" class="btn btn-success" style="color: #FFFFFF; font-weight: bold"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span> Переголосовать</button>
                        <button id="finishVoting" type="button" class="btn btn-danger" style="color: #FFFFFF; font-weight: bold"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span> Прервать Собрание</button>
                    </div>
                {{/response.isCanRestartVoting}}
                <div style="clear: both; margin-bottom: 5px;"></div>
            </div>
        {{/response.stateVotingText}}
        {{#batchVotingFinished}}
             <div style="float: right;"><a style="margin: 0px 0px 10px 0px; display: block; font-size: 30px;" href="/votingsystem/batchVotingResult.html?batchVotingId={{batchVotingId}}">Перейти к результатам собрания>>></a></div>
                        <div style="clear: both;"></div>
        {{/batchVotingFinished}}
        <div class="panel panel-default" style="padding: 15px 0px 15px 0px;">
            <div class="col-xs-12" >
                {{#response.nextVotingId}}
                    {{^response.stateVotingPaused}}
                        <div style="float: right;"><a style="margin: 0px 0px 10px 0px; display: block; font-size: 30px;" href="{{response.nextVotingLink}}">Перейти к следующему голосованию >>></a></div>
                        <div style="clear: both;"></div>
                    {{/response.stateVotingPaused}}
                {{/response.nextVotingId}}
                {{#response.errorMessage}}
                    <h3>Причина завершения голосования: {{response.errorMessage}}</h3>
                {{/response.errorMessage}}
                <div id="votingProtocolContent"></div>
            </div>
            <div style="clear: both;"></div>
        </div>
    {{/errorCode}}
</script>
<script id="votingProtocolProContraAbstainTemplate" type="x-tmpl-mustache">
    {{#response.votingItems}}
        {{#isWinner}}
            <div class="votingItem winnerVotingItem">
                <div style="width: 100%;">
                    {{#isProItem}}
                        {{^response.stateVotingText}}
                            <div style="float: right; color: #FF0000;"><h4>{{{response.votingWinnerText}}}</h4></div>
                            <div style="clear: both;"></div>
                        {{/response.stateVotingText}}
                    {{/isProItem}}
                    <div class="protocolItemContent">
                        <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                    </div>
                </div>
            </div>
        {{/isWinner}}
        {{^isWinner}}
            <div class="votingItem lostVotingItem">
                <div style="width: 100%;">
                    <div class="protocolItemContent">
                        <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                    </div>
                </div>
            </div>
        {{/isWinner}}
        <div>
            <h3 style="display: inline;">Проголосовали за данный вариант</h3>
            <a href="#" onclick="toggleProtocol('{{id}}'); return false;">(Свернуть / Развернуть)</a>
            <div class="votingProtocol" id="votingProtocol{{id}}-grid"></div>
            <hr/>
        </div>
    {{/response.votingItems}}
</script>
<script id="votingProtocolCandidateTemplate" type="x-tmpl-mustache">
    {{#response.votingItems}}
        {{#isWinner}}
            {{#isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div class="candidateItem">
                        <a href="/sharer/{{candidate.ikp}}">
                            <img data-src="holder.js/140x/140" alt="140x140"
                                 src="{{candidate.avatarBigSrc}}" data-holder-rendered="true"
                                 class="media-object img-thumbnail tooltiped-avatar"
                                 data-sharer-ikp="{{candidate.ikp}}" data-placement="left">
                        </a>
                    </div>
                    <div class="votingResultBlock">
                        <div style="float: left;"><h4>{{candidate.name}}</h4></div>
                        {{^response.stateVotingText}}
                            <div style="float: right; color: #FF0000;"><h4>{{{response.votingWinnerText}}}</h4></div>
                            <div style="clear: both;"></div>
                        {{/response.stateVotingText}}
                        <div class="protocolItemContent">
                            <span>ЗА - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        {{^isWinner}}
            {{#isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div class="candidateItem">
                        <a href="/sharer/{{candidate.ikp}}">
                            <img data-src="holder.js/100x/100" alt="100x100"
                                 src="{{candidate.avatarMediumSrc}}" data-holder-rendered="true"
                                 class="media-object img-thumbnail tooltiped-avatar"
                                 data-sharer-ikp="{{candidate.ikp}}" data-placement="left">
                        </a>
                    </div>
                    <div class="votingResultBlock">
                        <h4>{{candidate.name}}</h4>
                        <div class="protocolItemContent">
                            <span>ЗА - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        <div>
            <h3 style="display: inline;">Проголосовали за данного кандидата</h3>
            <a href="#" onclick="toggleProtocol('{{id}}'); return false;">(Свернуть / Развернуть)</a>
            <div class="votingProtocol" id="votingProtocol{{id}}-grid"></div>
            <hr/>
        </div>
    {{/response.votingItems}}
</script>
<script id="votingProtocolSingleSelectionTemplate" type="x-tmpl-mustache">
    {{#response.votingItems}}
        {{#isWinner}}
            {{#isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        {{^response.stateVotingText}}
                            <div style="float: right; color: #FF0000;"><h4>{{{response.votingWinnerText}}}</h4></div>
                            <div style="clear: both;"></div>
                        {{/response.stateVotingText}}
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        {{^isWinner}}
            {{#isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        <div>
            <h3 style="display: inline;">Проголосовали за данный вариант</h3>
            <a href="#" onclick="toggleProtocol('{{id}}'); return false;">(Свернуть / Развернуть)</a>
            <div class="votingProtocol" id="votingProtocol{{id}}-grid"></div>
            <hr/>
        </div>
    {{/response.votingItems}}
</script>
<script id="votingProtocolInterviewTemplate" type="x-tmpl-mustache">
 {{^response.parameters.isAllowedVote}}
   {{#response.valid}}
 <h4>Предложенные варианты:</h3>
  {{#response.votingItems}}
  {{^isAbstain}}
   <div class="votingItem">
    <div style="width: 100%;">
     <div class="protocolItemContent">
         <span>{{value}}</span>
      </div>
     </div>
   </div>
    {{/isAbstain}}
 {{/response.votingItems}}
 {{/response.valid}}
  {{/response.parameters.isAllowedVote}}
    {{#response.parameters.isAllowedVote}}
    {{#response.votingItems}}
        {{#isWinner}}
            {{#isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        {{^response.stateVotingText}}
                            <div style="float: right; color: #FF0000;"><h4>{{{response.votingWinnerText}}}</h4></div>
                            <div style="clear: both;"></div>
                        {{/response.stateVotingText}}
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        {{^isWinner}}
            {{#isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        <div>
            <h3 style="display: inline;">Проголосовали за данный вариант</h3>
            <a href="#" onclick="toggleProtocol('{{id}}'); return false;">(Свернуть / Развернуть)</a>
            <div class="votingProtocol" id="votingProtocol{{id}}-grid"></div>
            <hr/>
        </div>
    {{/response.votingItems}}
    {{/response.parameters.isAllowedVote}}
</script>
<script id="votingProtocolMultipleSelectionTemplate" type="x-tmpl-mustache">
    {{#response.votingItems}}
        {{#isWinner}}
            {{#isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem winnerVotingItem">
                    <div style="width: 100%;">
                        {{^response.stateVotingText}}
                            <div style="float: right; color: #FF0000;"><h4>{{{response.votingWinnerText}}}</h4></div>
                            <div style="clear: both;"></div>
                        {{/response.stateVotingText}}
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        {{^isWinner}}
            {{#isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
            {{^isAbstain}}
                <div class="votingItem lostVotingItem">
                    <div style="width: 100%;">
                        <div class="protocolItemContent">
                            <span>{{value}} - {{votesPercentFormatted}} {{^isClosedVoting}}({{countVotes}} {{countVotesStringForm}} из {{countVoters}}){{/isClosedVoting}}</span>
                        </div>
                    </div>
                </div>
            {{/isAbstain}}
        {{/isWinner}}
        <div>
            <h3 style="display: inline;">Проголосовали за данный вариант</h3>
            <a href="#" onclick="toggleProtocol('{{id}}'); return false;">(Свернуть / Развернуть)</a>
            <div class="votingProtocol" id="votingProtocol{{id}}-grid"></div>
            <hr/>
        </div>
    {{/response.votingItems}}
</script>



<script id="votingTemplate" type="x-tmpl-mustache">
    {{#errorCode}}
        <div>Произошла ошибка. Текст ошибки: {{message}}</div>
    {{/errorCode}}
    {{^errorCode}}
        <div>
            <div style="float: left;">
                <h3>Тема голосования:</h3>
            </div>
            <div style="float: right;">
                <h3 id="leftTimeEndOfVotingSpan"></h3>
            </div>
            <div style="clear: both;"></div>
        </div>
        <div id="descriptionBlock">
            <pre id="descriptionPre">{{{response.additionalData.description}}}</pre>
        </div>
        <div class="form-group shadow" id="descriptionShadow"></div>
        <div class="form-group text-right">
            <a href="#" id="descriptionShow" class="btn btn-xs btn-default">Развернуть</a>
        </div>
        <div style="padding: 5px 0px;">
            {{#response.buttonsContent}}
                <button type="button" class="btn btn-primary buttonText" style="margin: 5px 5px 5px 0px;">{{buttonText}}</button>
            {{/response.buttonsContent}}
        </div>
        <div class="panel panel-default" style="padding: 15px 0px 15px 0px;">
            <div class="col-xs-12" >
                <label id="votersListLabel">Список участников голосования. Проголосовали {{response.votersWhoVotesCount}} из {{response.votersCount}}.</label>
                <div id="voters_container" style="border: 1px solid #ddd; padding: 5px 5px 5px 5px;">
                    {{#response.voters}}
                        <div id="voter_div_{{id}}" class="voterBlock {{#isVote}}voterBlockIsVote{{/isVote}}{{^isVote}}voterBlockIsNotVote{{/isVote}}">
                            <a href="/sharer/{{ikp}}">
                                <img data-src="holder.js/90x/90" alt="90x90"
                                     src="{{avatarMediumSrc}}" data-holder-rendered="true"
                                     class="media-object img-thumbnail tooltiped-avatar"
                                     data-sharer-ikp="{{ikp}}" data-placement="left">
                            </a>
                            <div class="hurryVoterBlock" voter_id="{{id}}" style="{{#isVote}}display: none;{{/isVote}}">
                                <button type="button" class="btn btn-default hurryInVoting" style="margin-top: 5px;">Поторопить</button>
                            </div>
                        </div>
                    {{/response.voters}}
                </div>
            </div>
            {{#response.isAllreadyVoting}}
                <div class="col-xs-12 allReadyVoting">
                    <label>Вы уже проголосовали</label>
                </div>
            {{/response.isAllreadyVoting}}
            {{^response.isAllreadyVoting}}
                {{^response.parameters.voteCancellable}}
                    <div class="col-xs-12">
                        <label>Данное голосование нельзя выполнить повторно!</label>
                    </div>
                {{/response.parameters.voteCancellable}}
            {{/response.isAllreadyVoting}}

            <div class="col-xs-12" id="votingTypeContent"></div>

            <div class="col-xs-12" style="text-align: center;">
                {{#response.isAllreadyVoting}}
                    {{#response.parameters.voteCancellable}}
                        <button type="button" class="btn btn-primary" id="saveVoting" style="margin-top: 5px; display: none;">{{#response.interview}}Подтвердить{{/response.interview}}{{^response.interview}}Подтвердить свой выбор{{/response.interview}}</button>
                        <button type="button" class="btn btn-primary" id="reVoting" style="margin-top: 5px;">Переголосовать</button>
                    {{/response.parameters.voteCancellable}}
                {{/response.isAllreadyVoting}}
                {{^response.isAllreadyVoting}}
                    <button type="button" class="btn btn-primary" id="saveVoting" style="margin-top: 5px;" disabled="true">{{#response.interview}}Подтвердить{{/response.interview}}{{^response.interview}}Подтвердить свой выбор{{/response.interview}}</button>
                {{/response.isAllreadyVoting}}
            </div>
            <div style="clear: both;"></div>
        </div>

    {{/errorCode}}

</script>

<script id="votingProContraAbstainSelectionTemplate" type="x-tmpl-mustache">
    {{^response.isAllreadyVoting}}
        <div>
            Вам необходимо выбрать и проголосовать за <b>{{response.parameters.maxSelectionCount}} {{response.maxSelectionCountStringForm}}.
            <span id="votingForNonAbstainItemsText">Вы уже проголосовали за: <span id="selectedCountVotingItems">0</span> из {{response.parameters.maxSelectionCount}}</span></b>
            <b><span id="votingForAbstainItemsText">Вы воздержались.</span></b>
        </div>
    {{/response.isAllreadyVoting}}
    <table class="table table-hover table-striped votingTable {{#response.isAllreadyVoting}}allreadyVotedTable{{/response.isAllreadyVoting}}" max_selection_count="{{response.parameters.maxSelectionCount}}">
        <thead>
            <tr>
                <th>Вариант голосования</th>
                <th style="width: 100px;">Ваш голос</th>
                <th style="width: 60px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#response.votingItems}}
                <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="{{isAbstain}}">
                    <td>{{value}}</td>
                    <td>
                        {{#response.isAllreadyVoting}}
                            {{#selectedVotingId}}
                                Вы выбрали этот вариант
                            {{/selectedVotingId}}
                        {{/response.isAllreadyVoting}}
                        {{^response.isAllreadyVoting}}
                            <div class="btn-group btn-group-sm">
                                {{#isAbstain}}
                                    <button class="btn btn-warning buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                {{/isAbstain}}
                                {{^isAbstain}}
                                    <button class="btn btn-success buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                {{/isAbstain}}
                            </div>
                        {{/response.isAllreadyVoting}}
                    </td>
                    <td>
                        {{^response.isAllreadyVoting}}
                            <div class="btn-group btn-group-sm" style="display: none;">
                                <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                            </div>
                        {{/response.isAllreadyVoting}}
                    </td>
                </tr>
            {{/response.votingItems}}
        </tbody>
    </table>
</script>
<script id="votingCandidateSelectionTemplate" type="x-tmpl-mustache">
    {{^response.isAllreadyVoting}}
        <div>
            Вам необходимо выбрать и проголосовать за <b>{{response.parameters.maxSelectionCount}} {{response.maxSelectionCountStringForm}}.
            <span id="votingForNonAbstainItemsText">Вы уже проголосовали за: <span id="selectedCountVotingItems">0</span> из {{response.parameters.maxSelectionCount}}</span></b>
            <b><span id="votingForAbstainItemsText">Вы воздержались.</span></b>
        </div>
    {{/response.isAllreadyVoting}}
    <table class="table table-hover table-striped votingTable {{#response.isAllreadyVoting}}allreadyVotedTable{{/response.isAllreadyVoting}}" max_selection_count="{{response.parameters.maxSelectionCount}}">
        <thead>
            <tr>
                <th style="width: 100px;"></th>
                <th>Имя кандидата</th>
                <th style="width: 100px;">Ваш голос</th>
                <th style="width: 60px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#response.votingItems}}
                {{^isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="false">
                        <td>
                            <a href="/sharer/{{candidate.ikp}}">
                                <img data-src="holder.js/90x/90" alt="90x90"
                                     src="{{candidate.avatarSrc}}" data-holder-rendered="true"
                                     class="media-object img-thumbnail tooltiped-avatar"
                                     data-sharer-ikp="{{candidate.ikp}}" data-placement="left">
                            </a>
                        </td>
                        <td>{{candidate.name}}</td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    За
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    За
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-success buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
            {{#response.votingItems}}
                {{#isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="true">
                        <td>{{value}}</td>
                        <td></td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    Воздержаться
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    Воздержаться
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-warning buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
        </tbody>
    </table>
</script>
<script id="votingInterviewTemplate" type="x-tmpl-mustache">
    {{^response.isAllreadyVoting}}
    {{#allowVote}}
        <div>
            Вам необходимо выбрать и проголосовать за <b>{{response.parameters.maxSelectionCount}} {{response.maxSelectionCountStringForm}}.
            <span id="votingForNonAbstainItemsText">Вы уже проголосовали за: <span id="selectedCountVotingItems">0</span> из {{response.parameters.maxSelectionCount}}</span></b>
            <b><span id="votingForAbstainItemsText">Вы воздержались.</span></b>
        </div>
      {{/allowVote}}
      {{^allowVote}}
      <div>
      Вам необходимо добавить один или несколько своих вариантов для последующего голосования. После завершения добавления вариантов нажмите кнопку "Подтвердить". Если вы не хотите предлагать своих вариантов,
      а сразу перейти к последующему голосованию, просто нажмите "Подтвердить", не добавляя своих вариантов.
      </div>
       {{/allowVote}}
    {{/response.isAllreadyVoting}}
     {{^response.isAllreadyVoting}}
    <table class="table table-hover table-striped votingTable {{#response.isAllreadyVoting}}allreadyVotedTable{{/response.isAllreadyVoting}}" max_selection_count="{{response.parameters.maxSelectionCount}}">
        <thead>
            <tr>
                <th>Вариант голосования</th>
                <th style="width: 100px;">Ваш голос</th>
                <th style="width: 60px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#response.votingItems}}
                {{^isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="false">
                        <td>{{value}}</td>
                        <td></td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-danger buttonDeleteVariant votingButton" data-voting-item-id={{id}} title="Удалить вариант"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
            {{#response.votingItems}}
                {{#isAbstain}}
                     <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="true">
                        <td>{{value}}</td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    Воздержаться
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    Воздержаться
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-warning buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
        </tbody>
    </table>
        <div class="addVoteItemContainer">
            <div>Предложить свой вариант:</div>
            <textarea id="voteItemText" style="width: 100%; height: 100px;"></textarea>
            <button type="button" class="btn btn-primary" id="addVoteItem" style="margin-top: 5px;">Добавить</button>
        </div>
    {{/response.isAllreadyVoting}}
</script>
<script id="votingSingleSelectionTemplate" type="x-tmpl-mustache">
    {{^response.isAllreadyVoting}}
        <div>
            Вам необходимо выбрать и проголосовать за <b>{{response.parameters.maxSelectionCount}} {{response.maxSelectionCountStringForm}}.
            <span id="votingForNonAbstainItemsText">Вы уже проголосовали за: <span id="selectedCountVotingItems">0</span> из {{response.parameters.maxSelectionCount}}</span></b>
            <b><span id="votingForAbstainItemsText">Вы воздержались.</span></b>
        </div>
    {{/response.isAllreadyVoting}}
    <table class="table table-hover table-striped votingTable {{#response.isAllreadyVoting}}allreadyVotedTable{{/response.isAllreadyVoting}}" max_selection_count="{{response.parameters.maxSelectionCount}}">
        <thead>
            <tr>
                <th>Вариант голосования</th>
                <th style="width: 100px;">Ваш голос</th>
                <th style="width: 60px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#response.votingItems}}
                {{^isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="false">
                        <td>{{value}}</td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    За
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    За
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-success buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
            {{#response.votingItems}}
                {{#isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="true">
                        <td>{{value}}</td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    Воздержаться
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    Воздержаться
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-warning buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
        </tbody>
    </table>
</script>
<script id="votingMultipleSelectionTemplate" type="x-tmpl-mustache">
    {{^response.isAllreadyVoting}}
        <div>
            {{response.selectionHint}} <b>{{response.maxSelectionCountStringForm}}.
            <span id="votingForNonAbstainItemsText">Вы уже проголосовали за: <span id="selectedCountVotingItems">0</span> из {{response.parameters.maxSelectionCount}}</span></b>
            <b><span id="votingForAbstainItemsText">Вы воздержались.</span></b>
        </div>
    {{/response.isAllreadyVoting}}
    <table class="table table-hover table-striped votingTable {{#response.isAllreadyVoting}}allreadyVotedTable{{/response.isAllreadyVoting}}" max_selection_count="{{response.parameters.maxSelectionCount}}" min_selection_count="{{response.parameters.minSelectionCount}}" is-multiple-selection="true">
        <thead>
            <tr>
                <th>Вариант голосования</th>
                <th style="width: 100px;">Ваш голос</th>
                <th style="width: 60px;"></th>
            </tr>
        </thead>
        <tbody>
            {{#response.votingItems}}
                {{^isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="false">
                        <td>{{value}}</td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    За
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    За
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-success buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
            {{#response.votingItems}}
                {{#isAbstain}}
                    <tr voting_item="{{id}}" style="{{#selectedVotingId}}border: 2px #059A36 solid;{{/selectedVotingId}}" is_abstain="true">
                        <td>{{value}}</td>
                        <td>
                            {{#response.isAllreadyVoting}}
                                {{#selectedVotingId}}
                                    Воздержаться
                                {{/selectedVotingId}}
                            {{/response.isAllreadyVoting}}
                            {{^response.isAllreadyVoting}}
                                <div style="display: none;" class="notAcceptVotingValue">
                                    Воздержаться
                                </div>
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-warning buttonAddVote votingButton" title="Проголосовать"><i class="fa fa-fw fa-plus"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                        <td>
                            {{^response.isAllreadyVoting}}
                                <div class="btn-group btn-group-sm" style="display: none;">
                                    <button class="btn btn-danger buttonDeleteVote votingButton" title="Удалить голос"><i class="fa fa-fw fa-times"></i></button>
                                </div>
                            {{/response.isAllreadyVoting}}
                        </td>
                    </tr>
                {{/isAbstain}}
            {{/response.votingItems}}
        </tbody>
    </table>

    <!--
    Доработать комментарии для всех видов голосований
    {{#response.parameters.voteCommentsAllowed}}
        <div>
            <label>Комментарий</label>
            <textarea name="{{id}}_comment" class="votingItemComment" style="width: 100%; height: 100px;"
                {{#response.isAllreadyVoting}}
                    disabled="disabled"
                {{/response.isAllreadyVoting}}
            >{{comment}}</textarea>
        </div>
    {{/response.parameters.voteCommentsAllowed}}
    -->

</script>