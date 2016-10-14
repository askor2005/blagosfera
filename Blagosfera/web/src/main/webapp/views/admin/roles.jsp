<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<h1>
	Управление правами доступа
</h1>
<hr />

<h2>
	${profile.fullName} [${profile.email}]
</h2>

<hr/>

<form role="form" method="post">

	<div class="row">
	
		<c:forEach items="${roles}" var="r">
			<div class="col-xs-4">
				<div class="checkbox">
					<label>
						<input type="checkbox" name="role_id" value="${r.id}" <c:if test="${profile.roles.contains(r)}">checked="checked"</c:if> > ${r.name}
					</label>
				</div>		
			</div>
		</c:forEach>
	
	</div>

	<hr/>

	<button type="submit" class="btn btn-default">Сохранить</button>

</form>