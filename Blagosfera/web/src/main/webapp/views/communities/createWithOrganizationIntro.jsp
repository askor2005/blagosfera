<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">
	.btn {
		white-space : normal;
	}
	p.community-create-info {
		line-height : 2em;
	}
</style>

<div>
	<h1>Объединение в рамках юридического лица</h1>
</div>

<hr/>

<div>
	<p>
		Объединение в рамках юридического лица может быть создано двумя путями:
	</p>
	<ul>
		<li>
			<strong>Зарегистрировать уже существующее юридическое лицо в Системе</strong> – зарегистрировать существующее юридическое лицо в Системе БЛАГОСФЕРА может только лицо, имеющее полномочия действовать от имени данного юридического лица согласно его Уставу, либо лицо, имеющее на это полномочия по доверенности.
		</li>
		<li>
			<strong>Создать новое юридическое лицо</strong> – Система БЛАГОСФЕРА позволяет идентифицированным пользователям Системы провести собрание и создать весь пакет документов, необходимых для регистрации юридического лица в государственном органе. Участниками вновь создаваемого юридического лица могут быть только идентифицированные пользователи Системы БЛАГОСФЕРА.
		</li>
	</ul>
</div>

<hr/>
<p style="text-align: center;"><strong>Выберите тип создаваемого Объединения</strong></p>
<div class="row">
	<div class="col-xs-6">
		<a href="/groups/create/with_organization" class="btn btn-lg btn-primary btn-block" style="font-size: 12px;">Зарегистрировать существующее юридическое лицо в Системе</a>
	</div>
	<div class="col-xs-6">
		<a href="/groups/create/organization" id="create-new-with-organization" class="btn btn-lg btn-primary btn-block" style="font-size: 12px;">Создать новое юридическое<br>лицо</a>
	</div>
</div>
<hr/>