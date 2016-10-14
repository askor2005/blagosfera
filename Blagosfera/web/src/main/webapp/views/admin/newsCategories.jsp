<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:url value="/js/admin/newsCategories.js" var="newsCategoriesJs" />
<script type="text/javascript" src="${newsCategoriesJs}"></script>

<h1>Категории новостей</h1>
<hr/>

<a href="#" class="btn btn-primary" id="add-news-category-button">Добавить новую категорию новостей</a>
<hr/>

<div id="alert-area"></div>
<div id='news-categories-grid'></div>

<!--Form and Modal Region-->
<form id="editNewsCategoryForm">
    <div class="modal fade" role="dialog" id="editNewsCategoryModal" aria-hidden="true">

        <div class="modal-dialog">

            <div class="modal-content">

                <div class="modal-header">
                    <h4 style="text-align: center;">Добавление новой категории новостей</h4>
                </div>

                <div class="modal-body">

                    <input type="hidden" id="field-id" name="id"/>
                    <input type="hidden" id="field-parent-id" name="parent_id"/>

                    <div class="form-group">
                        <label for="field-title" >Введите наименование</label>
                        <input class="form-control" id="field-title" name="text" placeholder="Наименование"/>
                    </div>

                    <div class="form-group">
                        <label for="field-description" >Введите описание</label>
                        <textarea class="form-control" id="field-description" rows="5" name="description"
                                  placeholder="Описание"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="field-key" >Введите наименование ключа</label>
                        <input class="form-control" id="field-key" placeholder="Наименование ключа" name="key"/>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                    <button type="button" class="btn btn-primary" id="save-news-category-button" style="float: left;"></button>
                    <button type="button" class="btn btn-danger" id="delete-news-category-button" style="display: none; float: left;">
                        Удалить
                    </button>
                </div>


            </div>

        </div>

    </div>
</form>
<!--End Form and Modal Region-->
