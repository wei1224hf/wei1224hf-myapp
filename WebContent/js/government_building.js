
var government_building = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__government_building__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				government_building.config = response;
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
	

	,gismode: false
	,grid: function(){
		var columns = [
		     { display: getIl8n("id"), name: 'id', isSort: true, hide:true }		    
		    ,{ display: getIl8n("code"), name: 'code', width:120 ,render: function(x,y,z){
		    	if(x.wkt.length<10){
		    		return "<span style='color:red'>"+z+"</span>";
		    	}
		    	return z;
		     }}
		    ,{ display: getIl8n("name"), name: 'name', width:120 }
		    ,{ display: getIl8n("government_building","owner"), name: 'owner', width:100 }
		    ,{ display: getIl8n("government_building","population"), name: 'population', width:100 }		
		    ,{ display: getIl8n("government_building","count_floor"), name: 'count_floor', width:100 , hide: true }
		    ,{ display: getIl8n("government_building","time_founded"), name: 'time_founded', width:100 , hide: true }
		    ,{ display: getIl8n("status"), name: 'government_building__status', width:100 }
		    ,{ display: getIl8n("government_building","owner_type"), name: 'government_building__owner_type', width:100 , hide: true }
		    ,{ display: getIl8n("type"), name: 'government_building__type', width:100 }
	    
		];
		var config = {
				 id: 'government_building__grid'
				,height:'100%'
				,pageSizeOptions: [10, 20, 30, 40, 50 ,2000]
				,columns: columns,  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
	                ,search: "{}"
				},
				url: config_path__government_building__grid,
				method: "POST"
				,onAfterShowData: function(){
					var data = this.data.Rows;
					if(top.$.ligerui.get("win_520150")){
						//地图开启中
			            var theGisDom = top.$.ligerui.get("win_520150").frame;
						theGisDom.mygis.draw(data);
					}					
				}				
				,toolbar: { items: []}
		};
		
		//配置列表表头的按钮,根据当前用户的权限来初始化
		var permission = [];
		for(var i=0;i<top.basic_user.permission.length;i++){
			if(top.basic_user.permission[i].code=='52'){
				permission = top.basic_user.permission[i].children;
				for(var j=0;j<permission.length;j++){
					if(permission[j].code=='5201'){
						permission = permission[j].children;
					}
				}				
			}
		}
		for(var i=0;i<permission.length;i++){
			var theFunction = function(){};
			if(permission[i].code=='520101'){
				//查询
				theFunction = government_building.search;
			}else if(permission[i].code=='520102'){
				//查看
				theFunction = function(){
					var selected = government_building.grid_getSelectOne();

                	var id = selected.id;
                    if(top.$.ligerui.get("government_building__view_"+id)){
                        top.$.ligerui.get("government_building__view_"+id).show();
                        return;
                    }					
					top.$.ligerDialog.open({ 
						url: 'government_building__view.html?id='+selected.id+'&random='+Math.random()
						,height: 350
						,width: 590
						,title: selected.name
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true						
						, id: 'government_building__view_'+selected.id
						, modal: false
					});	
					
			        top.$.ligerui.get("government_building__view_"+selected.id).close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_building__view_"+selected.id));
			        };
				};
				
			}else if(permission[i].code=='520111'){
				//导入
				theFunction = government_building.upload;
			}else if(permission[i].code=='520112'){
				//导出
				theFunction = government_building.download;
			}else if(permission[i].code=='520121'){
				//添加
				theFunction = function(){
					if(top.$.ligerui.get("win_51")){
			            //地图存在,则根据地图绘制
			            var theGisDom = top.$.ligerui.get("win_51").frame;
			           
						theGisDom.enableDraw();
						theGisDom.afterDraw = function(data){
							top.$.ligerDialog.open({ 
								 url: 'government_building__add.html?gisid='+data
								,height: 400
								,width: 400
								,isResize: true
								,isHidden: false
								,title:  getIl8n("government_building","building") + " " + getIl8n("add")
							});	
						};
						
			            return;
			        }				
			        	
					top.$.ligerDialog.open({ 
						 url: 'government_building__add.html'
						,height: 400
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "government_building__add"
						, modal: false
						,title:  getIl8n("government_building","building") + " " + getIl8n("add")
					});	
					
			        top.$.ligerui.get("government_building__add").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_building__add"));
			        };					
				};
			}else if(permission[i].code=='520122'){
				//修改 	                					
				theFunction = function(){
	            	if(top.$.ligerui.get("government_building__modify")){
	            		alert("close first");return;
	            	}else{
						var selected = government_building.grid_getSelectOne();
	            		var id = selected.id;
	            	}					
	            	/*
	            	if(selected.socialworker!=top.basic_user.loginData.username){
	            		alert(getIl8n("noPermissionOnThis"));return;
	            	}
	            	*/
					
					top.$.ligerDialog.open({ 
						 url: 'government_building__modify.html?id='+id+"&for=bar"
						,height: 400
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "government_building__modify"
						, modal: false
						,title:  getIl8n("government_building","building") + "" + getIl8n("modify")						
					});	
					
			        top.$.ligerui.get("government_building__modify").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("government_building__modify"));
			        };						
				};
			}else if(permission[i].code=='520123'){
				//删除
				theFunction = government_building.remove;
				config.checkbox = true;
			}else if(permission[i].code=='520150'){
				//地图定位
				theFunction = government_building.locate;
			}else if(permission[i].code=='520151'){
				//绑定
				theFunction = government_building.bind;
			}
			else if(permission[i].code=='520192'){
				//统计
				theFunction = government_building.statistics_dialog;
			}
			
			config.toolbar.items.push({line: true });
			config.toolbar.items.push({
				text: permission[i].name , img:permission[i].icon , click : theFunction , id: permission[i].code
			});
		}
		
		$(document.body).ligerGrid(config);
	}
	
	,grid_getSelectOne: function(){
		var selected;
		if($.ligerui.get('government_building__grid').options.checkbox){
			selected = $.ligerui.get('government_building__grid').getSelecteds();
			if(selected.length!=1){ 
				alert(getIl8n("selectOne") );
				return;
			}
			selected = selected[0];
		}else{
			selected = $.ligerui.get('government_building__grid').getSelected();
			if(selected==null){
				alert(getIl8n("selectOne"));
				return;
			}
		}	
		return selected;
	}
	
	,remove: function(ids){
		if(!ids){
			//判断 ligerGrid 中,被勾选了的数据
			var selected = $.ligerui.get('government_building__grid').getSelecteds();
			//如果一行都没有选中,就报错并退出函数
			if(selected.length==0){alert(top.getIl8n('noSelect'));return;}
			//弹框让用户最后确认一下,是否真的需要删除.一旦删除,数据将不可恢复
			
				var ids = "";
				//遍历每一行元素,获得 id 
				for(var i=0; i<selected.length; i++){
					ids += selected[i].id+",";
				}
				ids = ids.substring(0,ids.length-1);				

		}
		if(!confirm( top.getIl8n('sureToDelete') )){
			return;
		}
		$.ajax({
			url: config_path__government_building__remove,
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
					$.ligerui.get('government_building__grid').loadData();
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
	
	,uploadPhoto: function(){		
		var path = $('#photo').val();
		
		top.$.ligerDialog.open({ 
			 content: "<iframe id='government_building_uploadPhoto_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action='"+config_path__government_building__uploadPhoto+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+"' target='send'><input name='file' type='file' /><input name='executor' value='"+top.basic_user.loginData.username+"' style='display:none' /><input name='session' value='"+top.basic_user.loginData.session+"' style='display:none' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form><img id='government_building_uploadPhoto_img' src='"+path+"' />"
			,height: 450
			,width: 400
			,isHidden: false
			,id: "government_building__uploadPhoto"
		});
		
		top.$.ligerui.get("government_building__uploadPhoto").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("government_building__uploadPhoto"));
        };	

		top.$("#government_building_uploadPhoto_if").load(function(){
	        var d = top.$("#government_building_uploadPhoto_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
	        	$('#photo').val(obj.path);
	        	top.$('#government_building_uploadPhoto_img').attr("src",obj.path);
	        }
	    });  
	}
	
	,upload: function(){		
				
		top.$.ligerDialog.open({ 
			 content: "<iframe id='government_building_upload_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action='"+
			 	config_path__government_building__upload+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+
			 	"' target='send'><input name='file' type='file' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form>"
			,height: 250
			,width: 400
			,isHidden: false
			,id: "government_building__upload"
		});
		
		top.$.ligerui.get("government_building__upload").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("government_building__upload"));
        };			

		top.$("#government_building_upload_if").load(function(){
	        var d = top.$("#government_building_upload_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
				alert(obj.msg);
	        }
	    }); 
	}	
	
	,download: function(){

		var data = $.ligerui.get('government_building__grid').options.parms;
		data.pagesize = $.ligerui.get('government_building__grid').options.pageSize;
		data.page = $.ligerui.get('government_building__grid').options.page;
		
		
		$.ajax({
			 url: config_path__government_building__download
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
					,id: "government_building__download"
				});
				
				top.$.ligerui.get("government_building__download").close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_building__download"));
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
	,add: function(gisid){

		var config = {
			id: 'government_building__add',
			fields: [
				 { display: top.getIl8n('government_building','zone_6'), name: "zone_6", type: "select", options :{data : government_building.config.zone_6, valueField : "code" , textField: "value", slide: false 			 	
				 } }
				,{ display: top.getIl8n('government_building','zone_8'), name: "zone_8", type: "select", options :{ valueField : "code" , textField: "value", slide: false
				 }}
				,{ display: top.getIl8n('government_building','zone_10'), name: "zone_10", type: "select", options :{ data : government_building.config.zone_10, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
				,{ display: top.getIl8n('government_building','owner'), name: "owner", type: "text", validate: { required:true } }		
				,{ display: top.getIl8n('government_building','count_floor'), name: "count_floor", type: "text", validate: { required:true } }		
				,{ display: top.getIl8n('government_building','owner_type'), name: "government_building__owner_type", type: "select", options :{data : government_building.config.government_building__owner_type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }		
				,{ display: top.getIl8n('type'), name: "government_building__type", type: "select", options :{data : government_building.config.government_building__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('status'), name: "government_building__status", type: "select", options :{data : government_building.config.government_building__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('photo'), name: "photo", type: "text" }		
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#photo").attr("disabled",true);
		$("#photo").css("background-color","#EEEEEE");
		$("#photo").parent().css("background-color","#EEEEEE");		
		$("#photo").parent().parent().next().append("&nbsp;<a href='#' onclick='government_building.uploadPhoto()' >"+ top.getIl8n('img')+"</a>");
		
		$('#zone_6').change(function(){
			var data = $('#zone_6_val').val();
			$.ajax({
				url: config_path__government_building__lowerCodes,
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
				url: config_path__government_building__lowerCodes,
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
		})		
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="government_building__submit" class="l-button l-button-submit" />' );
		
		$.ligerui.get('zone_10').setValue('2102041303');
		$.ligerui.get('name').setValue('住房楼宇');
		$.ligerui.get('owner').setValue('张三');
		$.ligerui.get('count_floor').setValue('2');
		$.ligerui.get('government_building__owner_type').setValue('10');
		$.ligerui.get('government_building__type').setValue('10');
		$.ligerui.get('government_building__status').setValue('10');
		
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
				$("#government_building__submit").attr("value",top.getIl8n('waitting'));
				
				var gisid = getParameter("gisid", window.location.toString() )
				if(gisid=="")gisid = "0";
				
				$.ajax({
					url: config_path__government_building__add,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 zone_10: $.ligerui.get('zone_10').getValue()
							,id_gis_polygon: ""+gisid
							,owner: $.ligerui.get('owner').getValue()
							,name: $.ligerui.get('name').getValue()
							,count_floor: $.ligerui.get('count_floor').getValue()
							,owner_type: $.ligerui.get('government_building__owner_type').getValue()
							,type: $.ligerui.get('government_building__type').getValue()
							,status: $.ligerui.get('government_building__status').getValue()
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
							$("#government_building__submit").remove();
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_building__submit").attr("value", top.getIl8n('submit') );
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
			id: 'government_building__modify',
			fields: [
				 
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
				,{ display: top.getIl8n('government_building','owner'), name: "owner", type: "text", validate: { required:true } }		
				,{ display: top.getIl8n('government_building','count_floor'), name: "count_floor", type: "text", validate: { required:true } }		
				,{ display: top.getIl8n('government_building','owner_type'), name: "government_building__owner_type", type: "select", options :{data : government_building.config.government_building__owner_type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }		
				,{ display: top.getIl8n('type'), name: "government_building__type", type: "select", options :{data : government_building.config.government_building__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('status'), name: "government_building__status", type: "select", options :{data : government_building.config.government_building__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('photo'), name: "photo", type: "text" }		
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#photo").attr("disabled",true);
		$("#photo").css("background-color","#EEEEEE");
		$("#photo").parent().css("background-color","#EEEEEE");		
		$("#photo").parent().parent().next().append("&nbsp;<a href='#' onclick='government_building.uploadPhoto()' >"+ top.getIl8n('img')+"</a>");		
		
		$('#zone_6').change(function(){
			var data = $('#zone_6_val').val();
			$.ajax({
				url: config_path__government_building__lowerCodes,
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
				url: config_path__government_building__lowerCodes,
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
		})	;	
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="government_building__submit" class="l-button l-button-submit" />' );
		
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
				$("#government_building__submit").attr("value",top.getIl8n('waitting'));
				
				var gisid = getParameter("gisid", window.location.toString() )
				if(gisid=="")gisid = "0";
				
				$.ajax({
					url: config_path__government_building__modify,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 id: getParameter("id", window.location.toString() )
							 
							,owner: $.ligerui.get('owner').getValue()
							,name: $.ligerui.get('name').getValue()
							,count_floor: $.ligerui.get('count_floor').getValue()
							,owner_type: $.ligerui.get('government_building__owner_type').getValue()
							,type: $.ligerui.get('government_building__type').getValue()
							,status: $.ligerui.get('government_building__status').getValue()
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
							$("#government_building__submit").remove();
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_building__submit").attr("value", top.getIl8n('submit') );
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
			url: config_path__government_building__view
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
				$.ligerui.get('count_floor').setValue(data.count_floor);
				$.ligerui.get('government_building__owner_type').setValue(data.owner_type);				
				$.ligerui.get('government_building__type').setValue(data.type);		
				$.ligerui.get('government_building__status').setValue(data.status);		
				$.ligerui.get('photo').setValue(data.photo);			
			}
		});
	}	

	,searchOptions: {}	
	,search: function(){
		var formD;
		if($.ligerui.get("formD")){
			formD = $.ligerui.get("formD");
			formD.show();
		}else{
			var form = $("<form id='form'></form>");
			var config = {
				 inputWidth: 170
				,labelWidth: 90
				,space: 40
				,fields: [
					 { display: top.getIl8n('name'), name: "government_building__name", newline: true, type: "text" }		
					,{ display: top.getIl8n('code'), name: "government_building__code", newline: true, type: "text", newline: false }					 
					,{ display: top.getIl8n('type'), name: "government_building__type", newline: true, type: "select", options :{data : government_building.config.government_building__type, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('status'), name: "government_building__status", newline: true, type: "select", options :{data : government_building.config.government_building__status , valueField : "code" , textField: "value" }, newline: false  }
					,{ display: top.getIl8n('government_building','owner_type'), name: "government_building__owner_type", newline: true, type: "select", options :{data : government_building.config.government_building__owner_type , valueField : "code" , textField: "value" } }

					,{ display: top.getIl8n('government_building','owner'), name: "government_building__owner", type: "text"}								
					
					,{ display: top.getIl8n('government_building','zone_2'), name: "government_building__zone_2", newline: true, type: "select", options :{data: government_building.config.zone_2, valueField : "code" , textField: "value" },group:"&nbsp;" }					
					,{ display: top.getIl8n('government_building','zone_4'), name: "government_building__zone_4", newline: true, type: "select", options :{valueField: "code" , textField: "value" }, newline: false   }					
					,{ display: top.getIl8n('government_building','zone_6'), name: "government_building__zone_6", newline: true, type: "select", options :{valueField: "code" , textField: "value" } }		
					,{ display: top.getIl8n('government_building','zone_8'), name: "government_building__zone_8", newline: true, type: "select", options :{valueField: "code" , textField: "value" }, newline: false   }					
					,{ display: top.getIl8n('government_building','zone_10'), name: "government_building__zone_10", newline: true, type: "select", options :{data: government_building.config.zone_10 , valueField : "code" , textField: "value" } }
					
					,{ display: top.getIl8n('government_building','time_founded'), name: "government_building__time_founded_big", type: "date",group: "&nbsp;" }		
					,{ display: top.getIl8n('government_building','time_founded'), name: "government_building__time_founded_small", type: "date", newline: false}
					,{ display: top.getIl8n('government_building','population'), name: "government_building__population_big", type: "number"} 	
					,{ display: top.getIl8n('government_building','population'), name: "government_building__population_small", type: "number", newline: false} 	
					,{ display: top.getIl8n('government_building','count_floor'), name: "government_building__count_floor_big", type: "number"} 	
					,{ display: top.getIl8n('government_building','count_floor'), name: "government_building__count_floor_small", type: "number", newline: false} 					

				]
			};
			$(form).ligerForm(config); 
			
			$.ligerDialog.open({
				 id: "formD"
				,width: 680
				,height: 430
				,content: form
				,title: top.getIl8n('search')
				,buttons : [
				    //清空查询条件
					{text: top.getIl8n('basic_user','clear'), onclick:function(){
						$.ligerui.get("government_building__grid").options.parms.search = "{}";
						$.ligerui.get("government_building__grid").loadData();
						
						$.ligerui.get("government_building__time_founded_big").setValue('');
						$.ligerui.get("government_building__time_founded_small").setValue('');
						$.ligerui.get("government_building__population_big").setValue('');
						$.ligerui.get("government_building__population_small").setValue('');
						$.ligerui.get("government_building__count_floor_big").setValue('');
						$.ligerui.get("government_building__count_floor_small").setValue('');
						$.ligerui.get("government_building__zone_2").setValue('');
						$.ligerui.get("government_building__zone_4").setValue('');
						$.ligerui.get("government_building__zone_6").setValue('');
						$.ligerui.get("government_building__zone_8").setValue('');
						$.ligerui.get("government_building__zone_10").setValue('');
						$.ligerui.get("government_building__owner_type").setValue('');

						$.ligerui.get("government_building__owner").setValue('');
						$.ligerui.get("government_building__name").setValue('');
						$.ligerui.get("government_building__code").setValue('');
						$.ligerui.get("government_building__type").setValue('');
						$.ligerui.get("government_building__status").setValue('');

					}},
					//提交查询条件
				    {text: top.getIl8n('search'), onclick:function(){
						var data = {};
						
						var doms = $("input[type='text']",$('#form'));
						for(var i=0;i<doms.length;i++){
							var theid = $(doms[i]).attr('id');
							if( theid=="government_building__zone_2"||
								theid=="government_building__zone_4"||
								theid=="government_building__zone_6"||
								theid=="government_building__zone_8"||
								theid=="government_building__zone_10"
							){
								var thevalue = $.ligerui.get(theid).getValue();
								if(thevalue!=null&&thevalue!=''){
									data.government_building__zone = thevalue;
								}
								continue;
							}
							var thekey = theid;
							var thetype = $(doms[i]).attr('ltype');							
						
							var thevalue = $.ligerui.get(theid).getValue();
							if(thetype=='date')thevalue = $('#'+theid).val();
							if(thevalue!="" && thevalue!=0 && thevalue!="0" && thevalue!=null){
								eval("data."+thekey+"='"+thevalue+"'");
							}
						}
						
						$.ligerui.get("government_building__grid").options.parms.search= $.ligerui.toJSON(data);
						$.ligerui.get("government_building__grid").loadData();
				}}]
			});
			
			$("#government_building__population_big").parent().parent().next().append("&nbsp;"+ top.getIl8n('big'));
			$("#government_building__population_small").parent().parent().next().append("&nbsp;"+ top.getIl8n('small'));				
			$("#government_building__count_floor_big").parent().parent().next().append("&nbsp;"+ top.getIl8n('big'));
			$("#government_building__count_floor_small").parent().parent().next().append("&nbsp;"+ top.getIl8n('small'));			
			
			$("#government_building__time_founded_big").parent().parent().parent().next().append("&nbsp;"+ top.getIl8n('big'));
			$("#government_building__time_founded_small").parent().parent().parent().next().append("&nbsp;"+ top.getIl8n('small'));	
			
			$('#government_building__zone_2').change(function(){
				var data = $('#government_building__zone_2_val').val();
				$.ajax({
					url: config_path__oa_person__lowerCodes,
					data: {
						code: data
						,reference: 'zone' 
						
		                ,executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
					}
					,type: "POST"
					,dataType: 'json'
					,contentType: "application/x-www-form-urlencoded; charset=gb2312"
					,success: function(response) {
						liger.get("government_building__zone_4").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});
			
			$('#government_building__zone_4').change(function(){
				var data = $('#government_building__zone_4_val').val();
				$.ajax({
					url: config_path__oa_person__lowerCodes,
					data: {
						code: data
						,reference: 'zone' 
						
		                ,executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
					}
					,type: "POST"
					,dataType: 'json'
					,contentType: "application/x-www-form-urlencoded; charset=gb2312"
					,success: function(response) {
						liger.get("government_building__zone_6").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});		
			
			$('#government_building__zone_6').change(function(){
				var data = $('#government_building__zone_6_val').val();
				$.ajax({
					url: config_path__oa_person__lowerCodes,
					data: {
						code: data
						,reference: 'zone' 
						
		                ,executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
					}
					,type: "POST"
					,dataType: 'json'
					,contentType: "application/x-www-form-urlencoded; charset=gb2312"
					,success: function(response) {
						liger.get("government_building__zone_8").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});	
			
			$('#government_building__zone_8').change(function(){
				var data = $('#government_building__zone_8_val').val();
				$.ajax({
					url: config_path__oa_person__lowerCodes,
					data: {
						code: data
						,reference: 'zone' 
						
		                ,executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
					}
					,type: "POST"
					,dataType: 'json'
					,contentType: "application/x-www-form-urlencoded; charset=gb2312"
					,success: function(response) {
						liger.get("government_building__zone_10").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});										
			
		}
	}
	

	,viewData: {}
	,view: function(){
		var id = getParameter("id", window.location.toString() );
    	$(document.body).html("<div id='menu'  ></div><div id='content' style='width:"+($(window).width()-170)+"px;margin-top:5px;'></div>");
    	var htmls = "";
    	$.ajax({
            url: config_path__government_building__view
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
            	government_building.viewData = response.data;
            	if(typeof(data.photo)=="undefined") data.photo = '../file/tavatar.gif';
            	for(var j in data){   
            		if(j=='sql'||j=='type'||j=='status')continue;
            		if(j=='photo'){
        				htmls += '<div style="position:absolute;right:5px;top:25px;background-color: rgb(220,250,245);width:166px;height:176px;"><img style="margin:2px;" src="'+data[j]+'" width="160" height="170" /></div>'
        				continue;
            		}
            		if(j=='id'||j=='remark')htmls+="<div style='width:100%;float:left;display:block;margin-top:5px;'/>";
            		            		
            		if(j=='remark'){
	            		eval("var key = getIl8n('"+j+"');");
	            		htmls += "<span class='view_lable' style='width:95%'>"+key+"</span><span style='width:95%' class='view_data'>"+data[j]+"</span>";
            		}else{
            			eval("var key = getIl8n('government_building','"+j+"');");
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
                    if(permission[i].code=='5201'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }   
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='520102'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }            
                
                for(var i=0;i<permission.length;i++){        
                	var theFunction = function(){};
                	if(permission[i].code=='52010232'){
                    	//拥有者 
                        theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: 'oa_person__view.html?id='+data.owner_person_id+'&f=b'
	       						,height: 500
	       						,width: 700
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						, title: data.owner
	       						,id: "oa_person__view"+data.owner_person_id
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("oa_person__view"+data.owner_person_id).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("oa_person__view"+data.owner_person_id));
	       			        };						
	       				};
                    }else if(permission[i].code=='52010245'){
                    	//做人社工 TODO
                       theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: 'oa_person__view.html?code='+government_building.viewData.socialworker+'&f=b'
	       						,height: 400
	       						,width: 400
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						,id: "oa_person__view"+id
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("oa_person__view"+id).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("oa_person__view"+id));
	       			        };						
	       				};
                    }else if(permission[i].code=='52010241'){
                    	//家庭列表
                        theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: "government_family__grid.html?search={building:'"+government_building.viewData.code+"'}&f=b"
	       						,height: 500
	       						,width: 800
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						, title: government_building.viewData.owner
	       						,id: "government_family__grid"+government_building.viewData.code
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("government_family__grid"+government_building.viewData.code).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("government_family__grid"+government_building.viewData.code));
	       			        };						
	       				};
                    }else if(permission[i].code=='52010233'){
                    	//居民列表
                        theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: "government_resident__grid.html?search={building:'"+government_building.viewData.code+"'}&f=b"
	       						,height: 500
	       						,width: 800
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						, title: government_building.viewData.name
	       						,id: "government_resident__grid"+government_building.viewData.code
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("government_resident__grid"+government_building.viewData.code).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("government_resident__grid"+government_building.viewData.code));
	       			        };						
	       				};
                    }else if(permission[i].code=='52010242'){
                    	//企业列表
                        theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: "government_company__grid.html?search={building:'"+government_building.viewData.code+"'}&f=b"
	       						,height: 500
	       						,width: 700
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						, title: data.name
	       						,id: "government_company__grid"+data.code
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("government_companyresident__grid"+data.code).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("government_company__grid"+data.code));
	       			        };						
	       				};
                    }
                    
                    items.push({line: true });	
					items.push({text: permission[i].name , img:permission[i].icon , click : theFunction});
                }                

                if(items.length>0){
	            	$("#menu").ligerToolBar({
	            		items:items
	            	});
                }else{
                	$('#menu').remove();
                }

            },
            error : function(){               
                alert(top.il8n.disConnect);
            }
        });
	}	
	
	,statistics_dialog: function(){
		var formD;
		if($.ligerui.get("dialog__statistics")){
			formD = $.ligerui.get("dialog__statistics");
			formD.show();
		}else{
			var form = $("<form id='form_statistics'></form>");
			$(form).ligerForm({
				inputWidth: 170
				,labelWidth: 90
				,space: 40
				,fields: [
					 { display: top.getIl8n('mode'), name: "statistics_dialog__mode", type: "select", options :{data : [
						 {"id":"chart","text": top.getIl8n('chart') },
						 {"id":"gis","text": top.getIl8n('GIS')},
					 ]} }
					,{ display: top.getIl8n('type'), name: "statistics_dialog__type", type: "select", options :{data : [
						{"id":"time","text":top.getIl8n('time')},
						{"id":"attribute","text":top.getIl8n('attribute')},
					] } }					 
					,{ display: top.getIl8n('attribute'), name: "statistics_dialog__attribute", type: "select" }
					
				]
			}); 

			$.ligerDialog.open({
				 id: "dialog__statistics"
				,width: 450
				,height: 250
				,content: form
				,title: top.getIl8n('statistics')
				,buttons : [
					{text: top.getIl8n('ok'), onclick:function(){
						var mode = $.ligerui.get('statistics_dialog__mode').getValue();
						var type = $.ligerui.get('statistics_dialog__type').getValue();
						var attribute = $.ligerui.get('statistics_dialog__attribute').getValue();
						var search = $.ligerui.toJSON( $.ligerui.get("government_building__grid").options.parms.search );

						var type = $.ligerui.get('statistics_dialog__type').getValue();

							
						var win = top.$.ligerDialog.open({ 
							 url: 'government_building__statistics.html?type='+type+'&mode='+mode+'&attribute='+attribute+'&search='+search+'&rand='+Math.random()
							, height: 250
							, width: 400
							, isHidden: false
							, showMax: true
							, showToggle: true
							, showMin: true						
							, modal: false
							, isResize: true
							, id: "government_building__statistics_time"
							, title: top.getIl8n('statistics')
						});
						
						win.max();
						
						win.close = function(){
				            var g = this;
				            top.$.ligerui.win.removeTask(this);
				            g.unmask();
				            g._removeDialog();
				            top.$.ligerui.remove(top.$.ligerui.get("government_building__statistics_time"));
				        };												
						
					}}]
			});	
			
			$('#statistics_dialog__mode').change(function(){				
				var data = $.ligerui.get('statistics_dialog__mode').getValue();
				
				if(data=='gis'){					
					liger.get("statistics_dialog__attribute").setData([
						 {id:"count",text:top.getIl8n('count')}
						,{id:"population",text: getIl8n("government_building","population") }
					]);
					$('#statistics_dialog__type').parent().parent().parent().parent().hide();	
				}
				
				if(data=='chart'){
					$('#statistics_dialog__type').parent().parent().parent().parent().show();
					$('#statistics_dialog__type').change(function(){				
						var data = $.ligerui.get('statistics_dialog__type').getValue();
						if(data=='time'){
							liger.get("statistics_dialog__attribute").setData([
								 {id:"day",text:top.getIl8n('day')}
								,{id:"month",text:top.getIl8n('month')}
								,{id:"year",text:top.getIl8n('year')}
							]);
						}
						if(data=='attribute'){
							liger.get("statistics_dialog__attribute").setData([
								 {"id":"type",text: top.getIl8n('type')}
								,{"id":"status",text: top.getIl8n('status')}
								,{"id":"owner_type",text: top.getIl8n('government_building','owner_type')}
								,{"id":"zone",text: top.getIl8n('zone')}
							]);
						}
					});
					
				}
			});	
		}
	}	
	
	,statistics: function(){
		var mode = getParameter("mode", window.location.toString() );
		if(mode=='chart')	government_building.statistics_chart();
		if(mode=='gis')		government_building.statistics_gis();
	}
	
	,statistics_chart: function(){
		var type = getParameter("type", window.location.toString() );		
		var attribute = getParameter("attribute", window.location.toString() );
		var search = getParameter("search", window.location.toString() );
		
		if(type=='time'){
			$.ajax({
				url: config_path__government_building__statistics_time
				,data: {					
					 search: search
					,attribute: attribute
					,executor: top.basic_user.loginData.username
					,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'						
				,success: function(response) {		
					$('body').highcharts({
			            chart: {
			                type: 'line',
			                marginRight: 130,
			                marginBottom: 25
			            },
			            title: {
			                text: ' ',
			                x: -20 //center
			            },
			            subtitle: {
			                text: ' ',
			                x: -20
			            },
			            xAxis: {
			                categories: response.xAxis
			            },
			            yAxis: {
			                title: {
			                    text: ' '
			                },
			                plotLines: [{
			                    value: 0,
			                    width: 1,
			                    color: '#808080'
			                }]
			            },
			            tooltip: {
			                valueSuffix: ' '
			            },
			            legend: {
			                layout: 'vertical',
			                align: 'right',
			                verticalAlign: 'top',
			                x: -10,
			                y: 100,
			                borderWidth: 0
			            },
			            series: [{
			                name: ' ',
			                data: response.series
			            }]
		        	});
		        	
		        	$('text:last').remove()
				}
			});
		}
		if(type=='attribute'){
			$.ajax({
				url: config_path__government_building__statistics_attribute
				,data: {					
					 search: search
					,attribute: attribute
					,executor: top.basic_user.loginData.username
					,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'						
				,success: function(response) {		

					var config = {
				        chart: {
				            plotBackgroundColor: null,
				            plotBorderWidth: null,
				            plotShadow: false
				        },
				        title: {
				            text:  "<b>"+top.getIl8n('government_building','building') +" "+ top.getIl8n('statistic')+"</b>"
				        },
				        tooltip: {
				    	    pointFormat: '{series.name}: <b>{point.y}</b>'
				        },
				        plotOptions: {
				            pie: {
				                allowPointSelect: true,
				                cursor: 'pointer',
				                dataLabels: {
				                    enabled: true,
				                    color: '#000000',
				                    connectorColor: '#000000',
				                    format: '<b>{point.name}</b>: {point.percentage:.1f} %, {point.y}'
				                }
				            }
				        },
				        series: [{
				            type: 'pie',
				            name: ' ',
				            data: response.data
				        }]
				    };
					if(attribute=="zone"){
						config = {
				            chart: {
				                type: 'column',
				                marginRight: 130,
				                marginBottom: 25
				            },
				            title: {
				                text: ' '
				            },
				            xAxis: {
				                categories: response.xAxis
				            },
				            yAxis: {
				                title: {
				                    text: ' '
				                },
				                plotLines: [{
				                    value: 0,
				                    width: 1,
				                    color: '#808080'
				                }]
				            },
				            tooltip: {
				                valueSuffix: ' '
				            },
				            legend: {
				                layout: 'vertical',
				                align: 'right',
				                verticalAlign: 'top',

				                borderWidth: 0
				            },
				            series: [{
				                name: '楼宇数量',
				                data: response.series,
				                dataLabels: {
				                    enabled: true,
				                    rotation: -90,
				                    color: '#FFFFFF',
				                    align: 'right',
				                    x: 4,
				                    y: 10,
				                    style: {
				                        fontSize: '13px',
				                        fontFamily: 'Verdana, sans-serif',
				                        textShadow: '0 0 3px black'
				                    }
				                }
				            },{
				                name: '人口总数',
				                data: response.series2,
				                dataLabels: {
				                    enabled: true,
				                    rotation: -90,
				                    color: '#FFFFFF',
				                    align: 'right',
				                    x: 4,
				                    y: 10,
				                    style: {
				                        fontSize: '13px',
				                        fontFamily: 'Verdana, sans-serif',
				                        textShadow: '0 0 3px black'
				                    }
				                }
				            }]
			        	};
					}

					$('body').highcharts(config);		        	
		        	$('text:last').remove();	
				}
			});
		}
		
	}	
	
	,statistics_gis: function(){
		var type = getParameter("type", window.location.toString() );		
		var attribute = getParameter("attribute", window.location.toString() );
		var search = getParameter("search", window.location.toString() );
		
		$('body').append("<div id='map' style='width:100%;height:100%;'></div> <div id='polygon_title'>polygon_title</div>");
		$.ajax({
			url: "/jsp/myapp.jsp?class=government_building&function=statistics_gis"
			,dataType: 'json'
		    ,type: "POST"
		    ,data: {
		         executor: top.basic_user.loginData.username
		        ,session: top.basic_user.loginData.session
		        
		        ,search: search
		        ,attribute: attribute
		    } 			
			,success : function(response) {
				
				var map = new OpenLayers.Map({
					div: "map",
					allOverlays: true
					
				});
				
				var vectorLayer =  new OpenLayers.Layer.Vector("Polygon Layer",{
					styleMap: new OpenLayers.StyleMap({
						 'default': {fillOpacity: 1, strokeOpacity: 1 , fillColor: "${color}" }
						,'temporary': {strokeColor: "black",strokeWidth:0.5,strokeOpacity: 0.8, fillColor: "#FFFFFF", fillOpacity: 1 }
					})});	
				
				map.addLayer(vectorLayer);
				map.addControl(new OpenLayers.Control.LayerSwitcher());
				map.addControl(new OpenLayers.Control.MousePosition());
				map.zoomToMaxExtent();
				
				
	            var list = [];
				var data = response.data;

				for(var i=0;i<data.length;i++){
					var wkt = data[i].wkt;
					var percent = Math.round( i*256/data.length );
					/*
					if(attribute=='population'){
						data[i].sum = parseInt(data[i].sum);
						var percent = parseInt(16+((maxnum-data[i].sum)/maxnum)*(16*16-16));
					}else{
						data[i].count = parseInt(data[i].count);
						var percent = parseInt(16+((maxnum-data[i].count)/maxnum)*(16*16-16));
					}
					*/
					
					var percent2 = 256-percent;
					percent2 = (percent2>=16)?percent2:16;
					percent2 = (percent2>=256)?255:percent2;
					percent2 = percent2.toString(16);
					
					percent = (percent>=16)?percent:16;
					percent = (percent>=256)?255:percent;
					percent = percent.toString(16);

					
					
					var color = "#"+percent+"00"+percent2;
	        		var polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.fromWKT(wkt),
	        				{
				   				 color: color
				   				,name: data[i].name
				   				,data: data[i].count
				   				,index: data.length-i
			   				}
	        		);
	           		list.push(polygonFeature);		
				}
				
				vectorLayer.addFeatures(list);
				map.setCenter(new OpenLayers.LonLat(114.92985, 25.83433), 16);
				var highlightCtrl = new OpenLayers.Control.SelectFeature(vectorLayer, {
				hover: true,
				highlightOnly: true,
				renderIntent: "temporary",
				eventListeners: {				
					featurehighlighted: function(f){
						var x = f.feature.geometry.getBounds().getCenterLonLat();
						var pos = map.getPixelFromLonLat(x);					
						
						$('#polygon_title').css("top",pos.y+"px").css("left",pos.x+"px").html(f.feature.attributes.name+"&nbsp;,&nbsp;总数:"+f.feature.attributes.data+"&nbsp;,&nbsp;排序:"+f.feature.attributes.index+"&nbsp;,&nbsp;");
						$('#polygon_title').show();
						
					}	
					,featureunhighlighted: function(f){
						$('#polygon_title').hide();	
					}
				}

			});
			map.addControl(highlightCtrl);
			highlightCtrl.activate();   
			
			

			}
			,error : function(){				
				
			}
		});	
					
	}
	
	,bind: function(){
		if(top.myglobal.building_bind == 'socialworker'){
			var selected = $.ligerui.get('government_building__grid').getSelecteds();
			var ids = "";
			for(var i=0;i<selected.length;i++){
				ids += ""+selected[i].id+",";
			}
			ids = ids.substring(0,ids.length-1);
			
			$.ajax({
				url: config_path__government_building__bind
				,dataType: 'json'
		        ,type: "POST"
		        ,data: {
		        	 ids: ids
		        	 
		        	,socialworker_id: top.myglobal.socialworker_id     	
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
		        } 			
				,success : function(response) {
					alert("ok");
				}
				,error : function(){				
					alert(top.il8n.disConnect);
				}
			});	
		}else{
	        if(top.$.ligerui.get("win_520151")){	        	
	            var theGisDom = top.$.ligerui.get("win_520151").frame;
				var selected = government_building.grid_getSelectOne();
				$.ajax({
					url: config_path__government_building__bound
					,dataType: 'json'
			        ,type: "POST"
			        ,data: {
			        	data: $.ligerui.toJSON({
			        		 id: selected.id
			        		,id_gis_polygon: theGisDom.mygis.bindItemId
			        		,name: selected.name
			        	})
			        	
		                ,executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
			        } 			
					,success : function(response) {
						alert("ok");
					}
					,error : function(){				
						alert(top.il8n.disConnect);
					}
				});					
	        }else{
	        	
				government_building.gismode = 1;
				var imgs = $('img');
				for(var i=0;i<imgs.length;i++){
					if ($(imgs[i]).parent().attr('toolbarid') != '520151'){
						$(imgs[i]).parent().remove();
					}
				}	
				$.ligerui.get('government_building__grid').toggleCol("code",false);		
				$.ligerui.get('government_building__grid').toggleCol("owner",false);		
				$.ligerui.get('government_building__grid').toggleCol("population",false);		
				$.ligerui.get('government_building__grid').toggleCol("government_building__status",false);		
				$.ligerui.get('government_building__grid').toggleCol("government_building__type",false);		
				
				top.$.ligerui.get('win_5201').set({ width: 300, height:top.desktop.winheight-35, left: 0, top: 0 });	        	
	        	
				var winheight = top.desktop.winheight;
				var winwidth = top.desktop.winwidth;
	
				var hei = winheight - 35;
				var wid = winwidth - 300;
				top_ = 0;
				left = 300;
				
	        	var win = top.$.ligerDialog.open({ 
					id : "win_520151"
					, isHidden:false 
					, height:  hei
					, url: "gis_bind.html?readserver=1"
					, top: top_
					, left: left
					, width: wid
					, showMax: true
					, showToggle: true
					, showMin: true
					, isResize: true
					, modal: false
					, title: "gis"
					, slide: false
				});
				
				
				win.close = function(){
					var g = this;
					top.$.ligerui.win.removeTask(this);
					g.unmask();
					g._removeDialog();
					top.$.ligerui.remove(top.$.ligerui.get("win_520151"));
					
				};
	        	return;
	        }
        }
	}
	
	,locate: function(){

	        if(top.$.ligerui.get("win_520150")){	        	
	            var theGisDom = top.$.ligerui.get("win_520150").frame;
				var selected = government_building.grid_getSelectOne();

                 if(selected.wkt==''){                        	
                 	return;
                 }
                 var wkt = selected.wkt;                        
                 var pos = wkt.substring(9,wkt.indexOf(" "));
                 var pos2 = wkt.substring(wkt.indexOf(" "),wkt.indexOf(",")); 
                 theGisDom.mygis.panTo(pos*1,pos2*1);

                 theGisDom.mygis.vectorLayer.drawFeature( theGisDom.mygis.vectorLayer.features[selected.__index] ,"temporary" );			
	        }else{
	        	
				government_building.gismode = 1;
				var imgs = $('img');
				for(var i=0;i<imgs.length;i++){
					if ($(imgs[i]).parent().attr('toolbarid') != '520150'){
						$(imgs[i]).parent().remove();
					}
				}	
				$.ligerui.get('government_building__grid').toggleCol("code",false);		
				$.ligerui.get('government_building__grid').toggleCol("owner",false);		
				$.ligerui.get('government_building__grid').toggleCol("population",false);		
				$.ligerui.get('government_building__grid').toggleCol("government_building__status",false);		
				$.ligerui.get('government_building__grid').toggleCol("government_building__type",false);		
				
				top.$.ligerui.get('win_5201').set({ width: 300, height:top.desktop.winheight-35, left: 0, top: 0 });	        	
	        	
				var winheight = top.desktop.winheight;
				var winwidth = top.desktop.winwidth;
	
				var hei = winheight - 35;
				var wid = winwidth - 300;
				top_ = 0;
				left = 300;
				
	        	var win = top.$.ligerDialog.open({ 
					id : "win_520150"
					, isHidden:false 
					, height:  hei
					, url: "gis_locate.html?readserver=1"
					, top: top_
					, left: left
					, width: wid
					, showMax: true
					, showToggle: true
					, showMin: true
					, isResize: true
					, modal: false
					, title: "gis"
					, slide: false
				});
				
				
				win.close = function(){
					var g = this;
					top.$.ligerui.win.removeTask(this);
					g.unmask();
					g._removeDialog();
					top.$.ligerui.remove(top.$.ligerui.get("win_520150"));
					
				};
	        	return;
	        }
        }
	
};
