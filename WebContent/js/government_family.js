/**
 * 用户模块,前端JS操作
 * 用户的 增删改查,用户组设置,密码修改,列表
 * 
 * @author wei1224hf@gmail.com
 * @license http://www.apache.org/licenses/LICENSE-2.0.html	APACHE2
 * @requires http://code.google.com/p/ligerui/ ligerui
 * @requires myApp.js mylib.js
 * */
var government_family = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__government_family__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				government_family.config = response;
				if ( typeof(afterAjax) == "string" ){
					eval(afterAjax);
				}else if( typeof(afterAjax) == "function"){
					afterAjax();
				}
			}
			,error : function(){				
				alert(top.il8n.disConnect);
			}
		});	
	}	
	
	/**
	 * 初始化页面列表
	 * 需要依赖一个空的 document.body 
	 * 表格的按钮依赖于用户的权限
	 * */
	,grid: function(){		
		
		var config = {
				id: 'government_family__grid'
				,height:'100%'
				,pageSizeOptions: [10, 20, 30, 40, 50 ,2000]
				,columns: [
				     { display: getIl8n("id"), name: 'id', isSort: true, hide:true }
				    ,{ display: getIl8n("government_family","zone_10"), name: 'zone_10', width:100 }
				    ,{ display: getIl8n("government_family","zone_8"), name: 'zone_8', width:100, hide: true }
				    ,{ display: getIl8n("government_family","zone_6"), name: 'zone_6', width:100, hide: true }
				    ,{ display: getIl8n("code"), name: 'code', width: 100, hide: true }
				    ,{ display: getIl8n("name"), name: 'name', width:120 }
				    ,{ display: getIl8n("government_family","owner"), name: 'owner', width: 80 }
				    ,{ display: getIl8n("government_family","count_member"), name: 'count_member', width: 50 }				    
				    ,{ display: getIl8n("status"), name: 'status_', width: 50 }
				    ,{ display: getIl8n("type"), name: 'type_', width: 100 }
			    
				],  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
				},
				url: config_path__government_family__grid,
				method: "POST"			
		};
		
		var search = getParameter("search", window.location.toString() );
		if(search!=""){
			config.parms.search = search;
		}else{
			config.parms.search = "{}";
		}		

		config.toolbar = { items: []};
		//配置列表表头的按钮,根据当前用户的权限来初始化
		var permission = [];
		for(var i=0;i<top.basic_user.permission.length;i++){
			if(top.basic_user.permission[i].code=='52'){
				permission = top.basic_user.permission[i].children;
				for(var j=0;j<permission.length;j++){
					if(permission[j].code=='5202'){
						permission = permission[j].children;
					}
				}				
			}
		}
		for(var i=0;i<permission.length;i++){
			var theFunction = null;
			if(permission[i].code=='520201'){
				//查询
				theFunction = government_family.search;
			}else if(permission[i].code=='520202'){
				//查看
				theFunction = function(){
					var selected = government_family.grid_getSelectOne();

                	var id = selected.id;
                    if(top.$.ligerui.get("government_family__view_"+id)){
                        top.$.ligerui.get("government_family__view_"+id).show();
                        return;
                    }					
					top.$.ligerDialog.open({ 
						url: 'government_family__view.html?id='+selected.id+'&random='+Math.random()
						,height: 350
						,width: 590
						,title: selected.name
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true						
						,id: 'government_family__view_'+selected.id
						, modal: false
					});	
					
			        top.$.ligerui.get("government_family__view_"+selected.id).close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_family__view_"+selected.id));
			        };
				}
				
			}else if(permission[i].code=='520211'){
				//导入
				theFunction = government_family.upload;
			}else if(permission[i].code=='520212'){
				//导出
				theFunction = government_family.download;
			}else if(permission[i].code=='520221'){
				//添加
				theFunction = function(){		
			        	
					top.$.ligerDialog.open({ 
						 url: 'government_family__add.html'
						,height: 500
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "government_family__add"
						, modal: false
					});	
					
			        top.$.ligerui.get("government_family__add").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_family__add"));
			        };					
				};
			}else if(permission[i].code=='520222'){
				//修改 	                					
				theFunction = function(){
	            	if(top.$.ligerui.get("government_family__modify")){
	            		alert("close first");return;
	            	}else{
						var selected = government_family.grid_getSelectOne();
	            		var id = selected.id;
	            	}		
	            	
	            	
					
					top.$.ligerDialog.open({ 
						 url: 'government_family__modify.html?id='+id+"&for=bar"
						,height: 400
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "government_family__modify"
						, modal: false
					});	
					
			        top.$.ligerui.get("government_family__modify").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_family__modify"));
			        };						
				}		
			}else if(permission[i].code=='520223'){
				//删除
				theFunction = government_family.remove;
				config.checkbox = true;
			}
			
			config.toolbar.items.push({line: true });
			config.toolbar.items.push({
				text: permission[i].name , img:permission[i].icon , click : theFunction
			});
		}
		
		$(document.body).ligerGrid(config);
	}
	
	,grid_getSelectOne: function(){
		var selected;
		if($.ligerui.get('government_family__grid').options.checkbox){
			selected = $.ligerui.get('government_family__grid').getSelecteds();
			if(selected.length!=1){ 
				alert(getIl8n("selectOne") );
				return;
			}
			selected = selected[0];
		}else{
			selected = $.ligerui.get('government_family__grid').getSelected();
			if(selected==null){
				alert(getIl8n("selectOne"));
				return;
			}
		}	
		return selected;
	}
	
	/**
	 * 删除一个或多个用户
	 * 如果用户拥有 删除权限 
	 * 则前端列表必定是一个带 checkBox 的
	 * */
	,remove: function(){
		//判断 ligerGrid 中,被勾选了的数据
		var selected = $.ligerui.get('government_family__grid').getSelecteds();
		//如果一行都没有选中,就报错并退出函数
		if(selected.length==0){alert(top.getIl8n('noSelect'));return;}
		//弹框让用户最后确认一下,是否真的需要删除.一旦删除,数据将不可恢复
		if(confirm( top.getIl8n('sureToDelete') )){
			var ids = "";
			//遍历每一行元素,获得 id 
			for(var i=0; i<selected.length; i++){
				ids += selected[i].id+",";
			}
			ids = ids.substring(0,ids.length-1);				
			
			$.ajax({
				url: config_path__government_family__remove,
				data: {
					ids: ids 
					
					//服务端权限验证所需
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					if(response.status=="1"){
						$.ligerui.get('government_family__grid').loadData();
					}else{
						alert(response.msg);
					}
				},
				error : function(){
					//net error,则删除按钮再也不能点了
					alert(top.getIl8n('disConnect'));
				}
			});				
		}		
	}
	
	,uploadPhoto: function(){		
		var path = $('#photo').val();
		
		top.$.ligerDialog.open({ 
			 content: "<iframe id='government_family_uploadPhoto_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+config_path__government_family__uploadPhoto+" target='send'><input name='file' type='file' /><input name='executor' value='"+top.basic_user.loginData.username+"' style='display:none' /><input name='session' value='"+top.basic_user.loginData.session+"' style='display:none' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form><br/><img id='government_family_uploadPhoto_img' style='border: 3px solid #A3C0E8;width: 360px;height: 360px;' src='"+path+"' />"
			,height: 450
			,width: 400
			,isHidden: false
			,id: "government_family__uploadPhoto"
		});
		
		top.$.ligerui.get("government_family__uploadPhoto").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("government_family__uploadPhoto"));
        };	

		top.$("#government_family_uploadPhoto_if").load(function(){
	        var d = top.$("#government_family_uploadPhoto_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
	        	$('#photo').val(obj.path);
	        	top.$('#government_family_uploadPhoto_img').attr("src",obj.path);
	        }
	    }); 
	    
	    
	}
	
	,upload: function(){		
		var path = $('#photo').val();
		
		top.$.ligerDialog.open({ 
			 content: "<iframe id='government_family_upload_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+
			 	config_path__government_family__upload+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+
			 	" target='send'><input name='file' type='file' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form>"
			,height: 250
			,width: 400
			,isHidden: false
			,id: "government_family__upload"
		});
		
		top.$.ligerui.get("government_family__upload").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("government_family__upload"));
        };			

		top.$("#government_family_upload_if").load(function(){
	        var d = top.$("#government_family_upload_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
				alert(obj.msg);
	        }
	    }); 
	}	
	
	,download: function(){

		var data = $.ligerui.get('government_family__grid').options.parms;
		data.pagesize = $.ligerui.get('government_family__grid').options.pageSize;
		data.page = $.ligerui.get('government_family__grid').options.page;
		
		
		$.ajax({
			 url: config_path__government_family__download
			,data: data
			,type: "POST"
			,dataType: 'json'
			,contentType: "application/x-www-form-urlencoded; charset=gb2312"
			,success: function(response) {
				top.$.ligerDialog.open({ 
					 content: "<a href='"+response.file+"' target='_blank'>download</a>"
					,height: 250
					,width: 400
					,isHidden: false
					,id: "government_family__download"
				});
				
				top.$.ligerui.get("government_family__download").close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_family__download"));
		        };		
			    		
			},
			error : function(){
				//net error,则删除按钮再也不能点了
				alert(top.getIl8n('disConnect'));
			}
		});	
	}
		
	/**
	 * 添加一个用户
	 * 前端以表单的形式向后台提交数据,服务端AJAX解析入库,
	 * 服务端还会反馈一些数据,比如 用户编号 等
	 * */
	,add: function(){

		var config = {
			id: 'government_family__add',
			fields: [
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
							
				,{ display: top.getIl8n('government_family','zone_6'), name: "zone_6", type: "select", options :{data : government_family.config.zone_6, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('government_family','zone_8'), name: "zone_8", type: "select", options :{ valueField : "code" , textField: "value", slide: false
				 }}
				,{ display: top.getIl8n('government_family','zone_10'), name: "zone_10", type: "select", options :{  valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('government_family','building'), name: "building", type: "select", options :{ valueField : "code" , textField: "value", slide: false }, validate: {required:true} }

				,{ display: top.getIl8n('government_family','time_founded'), name: "time_founded", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_family','time_over'), name: "time_over", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_family','income'), name: "income", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('government_family','count_member'), name: "count_member", type: "text", validate: { required:true } }
				
				,{ display: top.getIl8n('government_family','owner'), name: "owner", type: "text", validate: { required:true } }			
				,{ display: top.getIl8n('type'), name: "government_family__type", type: "select", options :{data : government_family.config.government_family__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('types'), name: "government_family__types", type: "select", options :{data : government_family.config.government_family__types,isShowCheckBox: true, isMultiSelect: true, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('status'), name: "government_family__status", type: "select", options :{data : government_family.config.government_family__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('photo'), name: "photo", type: "text" }		
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		$("#photo").attr("disabled",true);
		$("#photo").css("background-color","gray");
		$("#photo").parent().css("background-color","gray");		
		$("#photo").parent().parent().next().append("&nbsp;<a href='#' onclick='government_family.uploadPhoto()' >"+ top.getIl8n('img')+"</a>");
		
		$('#zone_6').change(function(){
			var data = $('#zone_6_val').val();
			$.ajax({
				url: config_path__government_family__lowerCodes,
				data: {
					code: data
					,reference: 'zone' 
					
					//服务端权限验证所需
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,contentType: "application/x-www-form-urlencoded; charset=gb2312"
				,success: function(response) {
					liger.get("zone_8").setData(response);
				},
				error : function(){
					//net error,则删除按钮再也不能点了
					alert(top.getIl8n('disConnect'));
				}
			});	
		});
		
		$('#zone_8').change(function(){
			var data = $('#zone_8_val').val();
			$.ajax({
				url: config_path__government_family__lowerCodes,
				data: {
					code: data
					,reference: 'zone' 
					
					//服务端权限验证所需
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					liger.get("zone_10").setData(response);
				},
				error : function(){
					//net error,则删除按钮再也不能点了
					alert(top.getIl8n('disConnect'));
				}
			});	
		});		
		
		$('#zone_10').change(function(){
			var data = $('#zone_10_val').val();
			$.ajax({
				url: config_path__government_family__lowerCodes,
				data: {
					code: data
					,reference: 'zone' 
					
					//服务端权限验证所需
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					liger.get("building").setData(response);
				},
				error : function(){
					//net error,则删除按钮再也不能点了
					alert(top.getIl8n('disConnect'));
				}
			});	
		});			
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="government_family__submit" class="l-button l-button-submit" />' );
		
		$.ligerui.get('zone_10').setValue('2102041303');
		$.ligerui.get('name').setValue('住房楼宇');
		$.ligerui.get('owner').setValue('张三');
		$.ligerui.get('time_founded').setValue('1960-01-01');
		$.ligerui.get('time_over').setValue('2050-01-01');
		$.ligerui.get('income').setValue('10000');
		$.ligerui.get('count_member').setValue('7');		
		$.ligerui.get('government_family__type').setValue('1');
		$.ligerui.get('government_family__types').setValue('1;2');
		$.ligerui.get('government_family__status').setValue('10');
		
		var v = $('#form').validate({
			debug: true,
			//JS前端验证错误
			errorPlacement: function (lable, element) {
				if (element.hasClass("l-text-field")) {
					element.parent().addClass("l-text-invalid");
				} 
			},
			//JS前端验证通过
			success: function (lable) {
				var element = $("[ligeruiid="+$(lable).attr('for')+"]",$("form"));
				if (element.hasClass("l-text-field")) {
					element.parent().removeClass("l-text-invalid");
				}
			},
			//提交表单,在表单内 submit 元素提交之后,要与后台通信
			submitHandler: function () {
				if(basic_user.ajaxState)return;
				basic_user.ajaxState = true;
				$("#government_family__submit").attr("value",top.getIl8n('waitting'));
				
				var gisid = getParameter("gisid", window.location.toString() )
				if(gisid=="")gisid = "0";
				
				$.ajax({
					url: config_path__government_family__add,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 building: $.ligerui.get('building').getValue()

							,owner: $.ligerui.get('owner').getValue()
							,name: $.ligerui.get('name').getValue()
							,time_founded: $("#time_founded").val()
							,time_over: $("#time_over").val()
							,income: $.ligerui.get('income').getValue()
							,count_member: $.ligerui.get('count_member').getValue()							

							,type: $.ligerui.get('government_family__type').getValue()
							,types: $.ligerui.get('government_family__types').getValue().replace(";",",")
							,status: $.ligerui.get('government_family__status').getValue()
							,photo: $.ligerui.get('photo').getValue()	
							
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#government_family__submit").remove();
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_family__submit").attr("value", top.getIl8n('submit') );
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
	}	
	
	//AJAX 通信状态,如果为TRUE,则表示服务端还在通信中	
	,ajaxState: false 	
	,modify: function(){
			
		var config = {
			id: 'government_family__add',
			fields: [
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
							
				,{ display: top.getIl8n('government_family','time_founded'), name: "time_founded", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_family','time_over'), name: "time_over", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_family','income'), name: "income", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('government_family','count_member'), name: "count_member", type: "text", validate: { required:true } }
				
				,{ display: top.getIl8n('government_family','owner'), name: "owner", type: "text", validate: { required:true } }			
				,{ display: top.getIl8n('type'), name: "government_family__type", type: "select", options :{data : government_family.config.government_family__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('types'), name: "government_family__types", type: "select", options :{data : government_family.config.government_family__types,isShowCheckBox: true, isMultiSelect: true, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('status'), name: "government_family__status", type: "select", options :{data : government_family.config.government_family__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('photo'), name: "photo", type: "text" }		
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		$("#photo").attr("disabled",true);
		$("#photo").css("background-color","gray");
		$("#photo").parent().css("background-color","gray");				
		$("#photo").parent().parent().next().append("&nbsp;<a href='#' onclick='government_family.uploadPhoto()' >"+ top.getIl8n('img')+"</a>");
		
		
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="government_family__submit" class="l-button l-button-submit" />' );
		
		var v = $('#form').validate({
			debug: true,
			//JS前端验证错误
			errorPlacement: function (lable, element) {
				if (element.hasClass("l-text-field")) {
					element.parent().addClass("l-text-invalid");
				} 
			},
			//JS前端验证通过
			success: function (lable) {
				var element = $("[ligeruiid="+$(lable).attr('for')+"]",$("form"));
				if (element.hasClass("l-text-field")) {
					element.parent().removeClass("l-text-invalid");
				}
			},
			//提交表单,在表单内 submit 元素提交之后,要与后台通信
			submitHandler: function () {
				if(basic_user.ajaxState)return;
				basic_user.ajaxState = true;
				$("#government_family__submit").attr("value",top.getIl8n('waitting'));
				
				var gisid = getParameter("gisid", window.location.toString() );
				if(gisid=="")gisid = "0";
				
				$.ajax({
					url: config_path__government_family__modify,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 id: getParameter("id", window.location.toString() )

							,owner: $.ligerui.get('owner').getValue()
							,name: $.ligerui.get('name').getValue()
							,time_founded: $("#time_founded").val()
							,time_over: $("#time_over").val()
							,income: $.ligerui.get('income').getValue()
							,count_member: $.ligerui.get('count_member').getValue()							

							,type: $.ligerui.get('government_family__type').getValue()
							,types: $.ligerui.get('government_family__types').getValue().replace(";",",")
							,status: $.ligerui.get('government_family__status').getValue()
							,photo: $.ligerui.get('photo').getValue()
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#government_family__submit").remove();
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_family__submit").attr("value", top.getIl8n('submit') );
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
		
		//从服务端读取信息,填充表单内容
		$.ajax({
			url: config_path__government_family__view
			,data: {
				id: getParameter("id", window.location.toString() )
				
				//服务端权限验证所需
				,executor: top.basic_user.loginData.username
				,session: top.basic_user.loginData.session
			}
			,type: "POST"
			,dataType: 'json'						
			,success: function(response) {	
			    var data = response.data;
				$.ligerui.get('name').setValue(data.name);				
				$.ligerui.get('owner').setValue(data.owner);

				$.ligerui.get('time_founded').setValue(data.time_founded);
				$.ligerui.get('time_over').setValue(data.time_over);
				$.ligerui.get('income').setValue(data.income);
				$.ligerui.get('count_member').setValue(data.count_member);
		
				$.ligerui.get('government_family__type').setValue(data.type);		
				$.ligerui.get('government_family__types').setValue(data.type);		
				$.ligerui.get('government_family__status').setValue(data.status);		
				$.ligerui.get('photo').setValue(data.photo);			
			}
		});
	}	

	
	//页面列表ligerUI控件	
	,searchOptions: {}	
	/**
	 * 与表格功能对应的 查询条件 
	 * 
	 * 查询条件有 用户名关键字,状态,类型,金币,用户组关键字
	 * */
	,search: function(){
		var formD;
		if($.ligerui.get("formD")){
			formD = $.ligerui.get("formD");
			formD.show();
		}else{
			var form = $("<form id='form'></form>");
			$(form).ligerForm({
				inputWidth: 170
				,labelWidth: 90
				,space: 40
				,fields: [
					 { display: top.getIl8n('type'), name: "government_family__search_type", newline: true, type: "select", options :{data : government_family.config.government_family__type, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('types'), name: "government_family__search_types", newline: true, type: "select", options :{data : government_family.config.government_family__types, valueField : "code" , textField: "value", isMultiSelect: true } }					 
					,{ display: top.getIl8n('status'), name: "government_family__search_status", newline: true, type: "select", options :{data : government_family.config.government_family__status , valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('government_family','zone_10'), name: "government_family__search_zone_10", newline: true, type: "select", options :{data : government_family.config.zone_10 , valueField : "code" , textField: "value" } }					
					
					,{ display: top.getIl8n('name'), name: "government_family__search_name", newline: true, type: "text" }
				]
			}); 
			$.ligerDialog.open({
				 id: "formD"
				,width: 350
				,height: 200
				,content: form
				,title: top.getIl8n('search')
				,buttons : [
				    //清空查询条件
					{text: top.getIl8n('basic_user','clear'), onclick:function(){
						$.ligerui.get("government_family__grid").options.parms.search = "{}";
						$.ligerui.get("government_family__grid").loadData();
						
						$.ligerui.get("government_family__search_type").setValue('');
						$.ligerui.get("government_family__search_types").setValue('');
						$.ligerui.get("government_family__search_zone_10").setValue('');
						$.ligerui.get("government_family__search_status").setValue('');
						$.ligerui.get("government_family__search_name").setValue('');
					}},
					//提交查询条件
				    {text: top.getIl8n('basic_user','search'), onclick:function(){
						var data = {};
						var  name =		$.ligerui.get("government_family__search_name").getValue()
						 	,type = 		$.ligerui.get("government_family__search_type").getValue()
						 	,types = 		$.ligerui.get("government_family__search_types").getValue().replace(/;/g,",")
						 	,zone_10 = 		$.ligerui.get("government_family__search_zone_10").getValue()
						 	,status = 		$.ligerui.get("government_family__search_status").getValue()
						 	;
						
						if(name!="")data.name = name;
						if(type!="")data.type = type;
						if(types!="")data.types = types;
						if(zone_10!="")data.zone_10 = zone_10;
						if(status!="")data.status = status;
						
						$.ligerui.get("government_family__grid").options.parms.search= $.ligerui.toJSON(data);
						$.ligerui.get("government_family__grid").loadData();
				}}]
			});
		}
	}
	
	/**
	 * 查看一个用户信息
	 * */
	,viewData: {}
	,view: function(){
		var id = getParameter("id", window.location.toString() );
    	$(document.body).html("<div id='menu'  ></div><div id='content' style='width:"+($(window).width()-170)+"px;margin-top:5px;'></div>");
    	var htmls = "";
    	$.ajax({
            url: config_path__government_family__view
            ,data: {
                id: id 
                ,executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
            },
            type: "POST",
            dataType: 'json',
            success: function(response) {
            	if(response.status!="1")return;
            	var data = response.data;

            	government_family.viewData = response.data;
            	if(typeof(data.photo)=="undefined") data.photo = '../file/tavatar.gif';
            	for(var j in data){   
            		if(j=='sql'||j=='type'||j=='status')continue;
            		if(j=='photo'){
        				htmls += '<div style="position:absolute;right:5px;top:25px;background-color: rgb(220,250,245);width:166px;height:176px;"><img style="margin:2px;" src="'+data[j]+'" width="160" height="170" /></div>'
        				continue;
            		}
            		if(j=='id'||j=='remark')htmls+="<div style='width:100%;float:left;display:block;margin-top:5px;'/>";
            		            		
            		if(j=='remark'||j=='types_'){
	            		eval("var key = getIl8n('government_family','"+j+"');");
	            		htmls += "<span class='view_lable' style='width:95%'>"+key+"</span><span style='width:95%' class='view_data'>"+data[j]+"</span>";
            		}else{
            			eval("var key = getIl8n('government_family','"+j+"');");
                		htmls += "<span class='view_lable'>"+key+"</span><span class='view_data'>"+data[j]+"</span>";
            		}
            	}; 
            	
            	$("#content").html(htmls);
            	            	
            	//查看详细,页面上也有按钮的
            	var items = [];            	
                var permission = top.basic_user.permission;
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='52'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }      
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='5202'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }   
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='520202'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }            
                
                for(var i=0;i<permission.length;i++){        
                	var theFunction = function(){};
                    
                	if(permission[i].code=='52020252'){
                    	//走访记录
                        theFunction = function(){};
                    }else if(permission[i].code=='52020233'){
                    	//居民列表
                        theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: "government_resident__grid.html?search={family:'"+data.code+"'}&f=b"
	       						,height: 500
	       						,width: 800
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						, title: data.owner
	       						,id: "government_resident__grid"+data.code
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("government_resident__grid"+data.code).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("government_resident__grid"+data.code));
	       			        };						
                        };
                    }
                    
                    items.push({line: true });	
					items.push({text: permission[i].name , img:permission[i].icon , click : theFunction});
                }                

            	$("#menu").ligerToolBar({
            		items:items
            	});

            },
            error : function(){               
                alert(top.il8n.disConnect);
            }
        });
	}
};