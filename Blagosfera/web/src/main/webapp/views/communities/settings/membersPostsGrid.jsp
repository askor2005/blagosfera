<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
	$(document).ready(function() {
		Ext.onReady(function () {
			storeMembersPosts = Ext.create('Ext.data.Store', {
				id		: 'storeMembersPosts',
				autoLoad: {start: 0, limit: 5},
				fields  : ['id', 'postId', 'userId', 'userName', 'communityName', 'name'/*, 'rightsExpiredDate', 'tools'*/],
				pageSize: 5,
				proxy: {
					type: 'ajax',
					url: loadMembersPostsLink,
					actionMethods: {
						read: 'POST'
					},
					reader: {
						type: 'json',
						rootProperty: 'items',
						totalProperty: 'total'
					}
				},
				listeners: {
					beforeload: function(store, options) {
						$("#membersPostsGridSearchResult").hide();
						/*store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
						store.proxy.extraParams.classId = selectedClassId;*/
					},
					load: function(component, dataList) {
						if (dataList == null || dataList.length == 0) {
							$("#membersPostsGridSearchResult").show();
							// Ничего не найдено
							$("#membersPostsGridSearchResult").text("По вашему запросу ничего не найдено.");
						}
					}
				}
			});
			Ext.create('Ext.grid.Panel', {
				id : 'membersPostsGrid',
				title: 'Должности',
				store: storeMembersPosts,
				columns: [{
					text     : 'Участник, ФИО',
					dataIndex: 'userName',
					flex: 1
				}, {
					text     : 'Объединение',
					dataIndex: 'communityName',
					flex: 1
				}, {
					text     : 'Должность',
					dataIndex: 'name',
					flex: 1
				}/*, {
					text     : 'Полномочия Истекают',
					dataIndex: 'rightsExpiredDate',
					flex: 1
				}*/, {
					text     : 'Управление',
					dataIndex: 'tools',
					flex: 1,
					renderer  : function(value, myDontKnow, record) {
						return '<a href="javascript:void(0)" onclick="fireMemberFromPost(' + record.data.userId + ',' + record.data.postId + ')">Уволить</a>';
					}
				}],
				tbar: [
				],
				dockedItems: [{
					xtype: 'pagingtoolbar',
					store: storeMembersPosts,
					dock: 'bottom',
					displayInfo: true,
					displayMsg: '{0} - {1} из {2}'
				}],
				viewConfig: {
					listeners: {
						refresh: function(gridview) {
						}
					}
				},
				listeners: {
				},
				frame: true,
				renderTo: 'membersPosts-grid'
			});
		});
	});
</script>