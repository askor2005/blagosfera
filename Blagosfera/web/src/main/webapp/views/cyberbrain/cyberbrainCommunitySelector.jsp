<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript">
    $(document).ready(function(){
        getCommunities();

        function getCommunities() {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{}",
                url: '/cyberbrain/sections/get_user_communities.json',
                success: function (response) {
                    if (response.success == true) {
                        response.items.forEach(function (entry) {
                            $("#combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }
    });
</script>

<!-- Modal -->
<div class="modal fade" id="communitySelectorModalWindow" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Выберите объединение для которого будет добавлена запись</h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <select id="combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" id="community-btn-add" class="btn btn-primary" onclick="">Добавить</button>
                <button type="button" id="community-btn-close" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div>
    </div>
</div>