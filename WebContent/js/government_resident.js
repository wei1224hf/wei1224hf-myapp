/**
 * 用户模块,前端JS操作
 * 用户的 增删改查,用户组设置,密码修改,列表
 * 
 * @author wei1224hf@gmail.com
 * @license http://www.apache.org/licenses/LICENSE-2.0.html	APACHE2
 * @requires http://code.google.com/p/ligerui/ ligerui
 * @requires myApp.js mylib.js
 * */
var government_resident = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__government_resident__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				government_resident.config = response;
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
				id: 'government_resident__grid'
				,height:'100%'
				,pageSizeOptions: [10, 20, 30, 40, 50 ,2000]
				,columns: [
				     { display: getIl8n("id"), name: 'id', isSort: true, hide:true }
				    ,{ display: getIl8n("government_resident","zone_10"), name: 'zone_10', width:120 }
				    ,{ display: getIl8n("code"), name: 'code', width: 100 }
				    ,{ display: getIl8n("name"), name: 'name', width:120 }
				    ,{ display: getIl8n("government_resident","job"), name: 'government_resident__job', width: 80 }
				    ,{ display: getIl8n("government_resident","relation"), name: 'relation_', width: 50 }				    
				    ,{ display: getIl8n("status"), name: 'status_', width: 50 }
				    ,{ display: getIl8n("type"), name: 'type_', width: 100 }
			    
				],  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
				},
				url: config_path__government_resident__grid,
				method: "POST"				
				,toolbar: { items: []}
		};
		
		var search = getParameter("search", window.location.toString() );
		if(search!=""){
			config.parms.search = search;
		}else{
			config.parms.search = "{}";
		}	
		
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
				theFunction = government_resident.search;
			}else if(permission[i].code=='520202'){
				//查看
				theFunction = function(){
					var selected = government_resident.grid_getSelectOne();

                	var id = selected.id;
                    if(top.$.ligerui.get("government_resident__view_"+id)){
                        top.$.ligerui.get("government_resident__view_"+id).show();
                        return;
                    }					
					top.$.ligerDialog.open({ 
						url: 'government_resident__view.html?id='+selected.id+'&random='+Math.random()
						,height: 350
						,width: 590
						,title: selected.name
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true						
						,id: 'government_resident__view_'+selected.id
						, modal: false
					}).max();	
					
			        top.$.ligerui.get("government_resident__view_"+selected.id).close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_resident__view_"+selected.id));
			        };
				}
				
			}else if(permission[i].code=='520211'){
				//导入
				theFunction = government_resident.upload;
			}else if(permission[i].code=='520212'){
				//导出
				theFunction = government_resident.download;
			}else if(permission[i].code=='520221'){
				//添加
				theFunction = function(){		
			        	
					top.$.ligerDialog.open({ 
						 url: 'government_resident__add.html'
						,height: 530
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "government_resident__add"
						, modal: false
						,title: getIl8n("government_resident","resident")+getIl8n("add")
					});	
					
			        top.$.ligerui.get("government_resident__add").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_resident__add"));
			        };					
				}
			}else if(permission[i].code=='520222'){
				//修改 	                					
				theFunction = function(){
	            	if(top.$.ligerui.get("government_resident__modify")){
	            		alert("close first");return;
	            	}else{
						var selected = government_resident.grid_getSelectOne();
	            		var id = selected.id;
	            	}					
					
					top.$.ligerDialog.open({ 
						 url: 'government_resident__modify.html?id='+id+"&for=bar"
						,height: 400
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "government_resident__modify"
						, modal: false
					});	
					
			        top.$.ligerui.get("government_resident__modify").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_resident__modify"));
			        };						
				}		
			}else if(permission[i].code=='520223'){
				//删除
				theFunction = government_resident.remove;
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
		if($.ligerui.get('government_resident__grid').options.checkbox){
			selected = $.ligerui.get('government_resident__grid').getSelecteds();
			if(selected.length!=1){ 
				alert(getIl8n("selectOne") );
				return;
			}
			selected = selected[0];
		}else{
			selected = $.ligerui.get('government_resident__grid').getSelected();
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
		var selected = $.ligerui.get('government_resident__grid').getSelecteds();
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
				url: config_path__government_resident__remove,
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
						$.ligerui.get('government_resident__grid').loadData();
					}else{
						alert(response.msg);
					}
				},
				error : function(){
					
					alert(top.getIl8n('disConnect'));
				}
			});				
		}		
	}
	
	,upload: function(){		
		
		top.$.ligerDialog.open({ 
			 content: "<iframe id='government_resident_upload_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+
			 	config_path__government_resident__upload+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+
			 	" target='send'><input name='file' type='file' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form>"
			,height: 250
			,width: 400
			,isHidden: false
			,id: "government_resident__upload"
		});
		
		top.$.ligerui.get("government_resident__upload").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("government_resident__upload"));
        };			

		top.$("#government_resident_upload_if").load(function(){
	        var d = top.$("#government_resident_upload_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
				alert(obj.msg);
	        }
	    }); 
	}	
	
	,download: function(){

		var data = $.ligerui.get('government_resident__grid').options.parms;
		data.pagesize = $.ligerui.get('government_resident__grid').options.pageSize;
		data.page = $.ligerui.get('government_resident__grid').options.page;
		
		
		$.ajax({
			 url: config_path__government_resident__download
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
					,id: "government_resident__download"
				});
				
				top.$.ligerui.get("government_resident__download").close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_resident__download"));
		        };		
			    		
			},
			error : function(){
				
				alert(top.getIl8n('disConnect'));
			}
		});	
	}
	
	,addPerson: function(){
		var win = top.$.ligerui.get("win_addPerson");
		if(win){			
			top.$.ligerui.win.addTask(win);
			win.show();
		}else{
			win = top.$.ligerDialog.open({ 
				  id : "win_addPerson"
				  
				, height: 500
				, url: "oa_person__add.html"
				, width: 700
				
				, isHidden: true 
				, showMax: true
				, showToggle: true
				, showMin: true
				, isResize: true
				, modal: false
				, title: "add person"
				, slide: false
				
			});		
		}
		
		win.hide = function(){				
			$.ligerui.get('person_id').setValue(top.myglobal.personid);
			top.$.ligerui.win.removeTask(this);
			this._hideDialog();
		};
	}
	
	,modifyPerson: function(){
		var win = top.$.ligerDialog.open({ 
			  id : "win_modifyPerson"
			  
			, height: 500
			, url: "oa_person__modify.html?id="+$('#person_id').val()
			, width: 700
			
			, isHidden: false 
			, showMax: true
			, showToggle: true
			, showMin: true
			, isResize: true
			, modal: false
			, title: "modify person"
			, slide: false
			
		});	
		
		win.close = function(){
			top.$.ligerui.win.removeTask(this);
			this.unmask();
			this._removeDialog();
			top.$.ligerui.remove(win);
		}	

	}	
		
	/**
	 * 添加一个用户
	 * 前端以表单的形式向后台提交数据,服务端AJAX解析入库,
	 * 服务端还会反馈一些数据,比如 用户编号 等
	 * */
	,add: function(){

		var config = {
			id: 'government_resident__add',
			fields: [
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
							
				,{ display: top.getIl8n('government_resident','zone_6'), name: "zone_6", type: "select", options :{data : government_resident.config.zone_6, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('government_resident','zone_8'), name: "zone_8", type: "select", options :{ valueField : "code" , textField: "value", slide: false}}
				,{ display: top.getIl8n('government_resident','zone_10'), name: "zone_10", type: "select", options :{  valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('government_resident','building'), name: "building", type: "select", options :{ valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('government_resident','family'), name: "family", type: "select", options :{ valueField : "code" , textField: "value", slide: false }, validate: { required:true } }
				
				,{ display: top.getIl8n('government_resident','time_in'), name: "time_in", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_resident','time_out'), name: "time_out", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_resident','person_id'), name: "person_id", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('government_resident','job'), name: "job", type: "text", validate: { required:true } }
				
				,{ display: top.getIl8n('government_resident','job_code'), name: "job_code", type: "select", options :{data : government_resident.config.industry, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('government_resident','relation'), name: "relation", type: "select", options :{data : government_resident.config.government_resident__relation, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
						
				,{ display: top.getIl8n('type'), name: "government_resident__type", type: "select", options :{data : government_resident.config.government_resident__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('types'), name: "government_resident__types", type: "select", options :{data : government_resident.config.government_resident__types,isShowCheckBox: true, isMultiSelect: true, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('status'), name: "government_resident__status", type: "select", options :{data : government_resident.config.government_resident__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }	
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		$("#person_id").attr("disabled",true);
		$("li:last",$("#person_id").parent().parent().parent()).css("width","80px").append("&nbsp;<a href='#' onclick='government_resident.addPerson()' >"+ top.getIl8n('government_resident','personInfo')+"</a>");
		
		$('#zone_6').change(function(){
			var data = $('#zone_6_val').val();
			$.ajax({
				url: config_path__government_resident__lowerCodes,
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
					
					alert(top.getIl8n('disConnect'));
				}
			});	
		});
		
		$('#zone_8').change(function(){
			var data = $('#zone_8_val').val();
			$.ajax({
				url: config_path__government_resident__lowerCodes,
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
					
					alert(top.getIl8n('disConnect'));
				}
			});	
		});		
		
		$('#zone_10').change(function(){
			var data = $('#zone_10_val').val();
			$.ajax({
				url: config_path__government_resident__lowerCodes,
				data: {
					 code: data
					,reference: 'zone' 
					
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					liger.get("building").setData(response);
				},
				error : function(){
					
					alert(top.getIl8n('disConnect'));
				}
			});	
		});	
		
		$('#building').change(function(){
			var data = $('#building_val').val();
			$.ajax({
				url: config_path__government_resident__lowerCodes,
				data: {
					code: data
					,reference: 'zone' 
					
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					liger.get("family").setData(response);
				},
				error : function(){
					
					alert(top.getIl8n('disConnect'));
				}
			});	
		});					
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="government_resident__submit" class="l-button l-button-submit" />' );

		var v = $('#form').validate({
			debug: true,
			errorPlacement: function (lable, element) {
				if (element.hasClass("l-text-field")) {
					element.parent().addClass("l-text-invalid");
				} 
			},
			success: function (lable) {
				var element = $("[ligeruiid="+$(lable).attr('for')+"]",$("form"));
				if (element.hasClass("l-text-field")) {
					element.parent().removeClass("l-text-invalid");
				}
			},
			submitHandler: function () {
				if(basic_user.ajaxState)return;
				basic_user.ajaxState = true;
				$("#government_resident__submit").attr("value",top.getIl8n('waitting'));
				
				var gisid = getParameter("gisid", window.location.toString() )
				if(gisid=="")gisid = "0";
				
				$.ajax({
					url: config_path__government_resident__add,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							family: $.ligerui.get('family').getValue()

							,time_in: $('#time_in').val()
							,time_out: $('#time_out').val()
							,name: $.ligerui.get('name').getValue()
							,person_id: $.ligerui.get('person_id').getValue()
							,types: $.ligerui.get('government_resident__types').getValue().replace(";",",")
							,type: $.ligerui.get('government_resident__type').getValue()
							,status: $.ligerui.get('government_resident__status').getValue()
							,job: $.ligerui.get('job').getValue()
							,job_code: $.ligerui.get('job_code').getValue()
							,relation: $.ligerui.get('relation').getValue()
							
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#government_resident__submit").attr("value", top.getIl8n('submit') );
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_resident__submit").remove();
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
		
		$.ligerui.get('name').setValue("Person name");
		$.ligerui.get('time_in').setValue("2013-05-01");
		$.ligerui.get('time_out').setValue("2013-07-01");
		$.ligerui.get('job').setValue("XXX软件公司当码畜");
		$.ligerui.get('job_code').setValue("B");
		$.ligerui.get('relation').setValue("00");
		$.ligerui.get('government_resident__type').setValue("2");
		$.ligerui.get('government_resident__status').setValue("1");
		$.ligerui.get('government_resident__types').setValue("10;13");
	}	
	
	//AJAX 通信状态,如果为TRUE,则表示服务端还在通信中	
	,ajaxState: false 	
	,modify: function(){
			
		var config = {
			id: 'government_resident__add',
			fields: [
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
							
				,{ display: top.getIl8n('government_resident','time_in'), name: "time_in", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_resident','time_out'), name: "time_out", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('government_resident','person_id'), name: "person_id", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('government_resident','job'), name: "job", type: "text", validate: { required:true } }
				
				,{ display: top.getIl8n('government_resident','job_code'), name: "job_code", type: "select", options :{data : government_resident.config.industry, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('government_resident','relation'), name: "relation", type: "select", options :{data : government_resident.config.government_resident__relation, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
						
				,{ display: top.getIl8n('type'), name: "government_resident__type", type: "select", options :{data : government_resident.config.government_resident__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('types'), name: "government_resident__types", type: "select", options :{data : government_resident.config.government_resident__types,isShowCheckBox: true, isMultiSelect: true, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('status'), name: "government_resident__status", type: "select", options :{data : government_resident.config.government_resident__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }	
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		$("#person_id").attr("disabled",true);
		$("li:last",$("#person_id").parent().parent().parent()).css("width","80px").append("&nbsp;<a href='#' onclick='government_resident.modifyPerson()' >"+ top.getIl8n('government_resident','personInfo')+"</a>");				
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="government_resident__submit" class="l-button l-button-submit" />' );

		var v = $('#form').validate({
			debug: true,
			errorPlacement: function (lable, element) {
				if (element.hasClass("l-text-field")) {
					element.parent().addClass("l-text-invalid");
				} 
			},
			success: function (lable) {
				var element = $("[ligeruiid="+$(lable).attr('for')+"]",$("form"));
				if (element.hasClass("l-text-field")) {
					element.parent().removeClass("l-text-invalid");
				}
			},
			submitHandler: function () {
				if(basic_user.ajaxState)return;
				basic_user.ajaxState = true;
				$("#government_resident__submit").attr("value",top.getIl8n('waitting'));
				
				var gisid = getParameter("gisid", window.location.toString() )
				if(gisid=="")gisid = "0";
				
				$.ajax({
					url: config_path__government_resident__modify,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 id: getParameter("id", window.location.toString() )

							,time_in: $('#time_in').val()
							,time_out: $('#time_out').val()
							,name: $.ligerui.get('name').getValue()
							,person_id: $.ligerui.get('person_id').getValue()
							,types: $.ligerui.get('government_resident__types').getValue().replace(";",",")
							,type: $.ligerui.get('government_resident__type').getValue()
							,status: $.ligerui.get('government_resident__status').getValue()
							,job: $.ligerui.get('job').getValue()
							,job_code: $.ligerui.get('job_code').getValue()
							,relation: $.ligerui.get('relation').getValue()
							
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#government_resident__submit").attr("value", top.getIl8n('submit') );
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_resident__submit").remove();
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
			url: config_path__government_resident__view
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
				$.ligerui.get('time_in').setValue(data.time_in);
				$.ligerui.get('time_out').setValue(data.time_out);
				$.ligerui.get('person_id').setValue(data.person_id);
				$.ligerui.get('job').setValue(data.job);
				$.ligerui.get('job_code').setValue(data.job_code);
				$.ligerui.get('relation').setValue(data.relation);
				$.ligerui.get('government_resident__type').setValue(data.type);
				$.ligerui.get('government_resident__status').setValue(data.status);
				$.ligerui.get('government_resident__types').setValue(data.types.replace(",",";"));		
								
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
					 { display: top.getIl8n('type'), name: "government_resident__search_type", newline: true, type: "select", options :{data : government_resident.config.government_resident__type, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('status'), name: "government_resident__search_status", newline: true, type: "select", options :{data : government_resident.config.government_resident__status , valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('government_resident','zone_10'), name: "government_resident__search_zone_10", newline: true, type: "select", options :{data : government_resident.config.zone_10 , valueField : "code" , textField: "value" } }					
					
					,{ display: top.getIl8n('name'), name: "government_resident__search_name", newline: true, type: "text" }
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
						$.ligerui.get("government_resident__grid").options.parms.search = "{}";
						$.ligerui.get("government_resident__grid").loadData();
						
						$.ligerui.get("government_resident__search_type").setValue('');
						$.ligerui.get("government_resident__search_zone_10").setValue('');
						$.ligerui.get("government_resident__search_status").setValue('');
						$.ligerui.get("government_resident__search_name").setValue('');
					}},
					//提交查询条件
				    {text: top.getIl8n('basic_user','search'), onclick:function(){
						var data = {};
						var  name =		$.ligerui.get("government_resident__search_name").getValue()
						 	,type = 		$.ligerui.get("government_resident__search_type").getValue()
						 	,zone_10 = 		$.ligerui.get("government_resident__search_zone_10").getValue()
						 	,status = 		$.ligerui.get("government_resident__search_status").getValue()
						 	;
						
						if(name!="")data.name = name;
						if(type!="")data.type = type;
						if(zone_10!="")data.zone_10 = zone_10;
						if(status!="")data.status = status;
						
						$.ligerui.get("government_resident__grid").options.parms.search= $.ligerui.toJSON(data);
						$.ligerui.get("government_resident__grid").loadData();
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
    	
    	var htmls = "";
    	$.ajax({
            url: config_path__government_resident__view
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
            	
            	government_resident.viewData = response.data;
            	for(var j in data){   

            		if(j=='id'||j=='remark')htmls+="<div style='width:100%;float:left;display:block;margin-top:5px;'/>";
            		            		
            		if(j=='remark'||j=='types_'||j=='job'){
	            		eval("var key = getIl8n('government_resident','"+j+"');");
	            		htmls += "<span class='view_lable' style='width:95%'>"+key+"</span><span style='width:95%' class='view_data'>"+data[j]+"</span>";
            		}else{
            			eval("var key = getIl8n('government_resident','"+j+"');");
                		htmls += "<span class='view_lable'>"+key+"</span><span class='view_data'>"+data[j]+"</span>";
            		}
            	}; 
            	$(document.body).html("<div id='menu'  ></div><div id='navtab' style='width:100%;margin-top:5px;'><div tabid='resident' id='resident' title='"+top.getIl8n('government_resident','resident')+"' ></div><div title='"+top.getIl8n('oa_person','person')+"' tabid='person' id='person' style='height:100%' ><iframe frameborder='0' name='person' src='oa_person__view.html?id="+data.person_id+"'></iframe></div>");
            	$("#navtab").ligerTab(); 
            	$("#resident").html(htmls);
            	            	
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
                    if(permission[i].code=='5203'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }   
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='520302'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }            
                
                for(var i=0;i<permission.length;i++){        
                	var theFunction = function(){};
                    if(permission[i].code=='52030223'){
                        theFunction = function(){};
                    }else if(permission[i].code=='52030222'){
                        theFunction = function(){};
                    }else if(permission[i].code=='52030290'){
                        theFunction = function(){};
                    }else if(permission[i].code=='52030291'){
                        theFunction = function(){};
                    }
                    
                    items.push({line: true });	
					items.push({text: permission[i].name , img:permission[i].icon , click : theFunction});
                }                
                if(items.length>0){
	            	$("#menu").ligerToolBar({
	            		items:items
	            	});
                }

            },
            error : function(){               
                alert(top.il8n.disConnect);
            }
        });
	}
};
