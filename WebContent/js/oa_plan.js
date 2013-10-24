
var oa_plan = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__oa_plan__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				oa_plan.config = response;
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
	
	,grid: function(){
		var config = {
				id: 'oa_plan__grid'
				,height:'100%'
				,pageSizeOptions: [10, 20, 30, 40, 50 ,2000]
				,columns: [
				     { display: getIl8n("id"), name: 'id', isSort: true, hide:true }
				    ,{ display: getIl8n("oa_plan","plan_time_start"), name: 'plan_time_start', width:120 }
				    ,{ display: getIl8n("oa_plan","plan_time_stop"), name: 'plan_time_stop', width:120 }
				    ,{ display: getIl8n("code"), name: 'code', width: 100 }
				    ,{ display: getIl8n("name"), name: 'name', width:120 }
			    
				    ,{ display: getIl8n("status"), name: 'status_', width: 50 }
				    ,{ display: getIl8n("type"), name: 'type_', width: 100 }
			    
				],  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
				},
				url: config_path__oa_plan__grid,
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
			if(top.basic_user.permission[i].code=='50'){
				permission = top.basic_user.permission[i].children;
				for(var j=0;j<permission.length;j++){
					if(permission[j].code=='5001'){
						permission = permission[j].children;
					}
				}				
			}
		}
		for(var i=0;i<permission.length;i++){
			var theFunction = null;
			if(permission[i].code=='500101'){
				//查询
				theFunction = oa_plan.search;
			}else if(permission[i].code=='500102'){
				//查看
				theFunction = function(){
					var selected = oa_plan.grid_getSelectOne();
	
	            	var id = selected.id;
	                if(top.$.ligerui.get("oa_plan__view_"+id)){
	                    top.$.ligerui.get("oa_plan__view_"+id).show();
	                    return;
	                }					
					top.$.ligerDialog.open({ 
						url: 'oa_plan__view.html?id='+selected.id+'&random='+Math.random()
						,height: 350
						,width: 590
						,title: selected.name
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true						
						,id: 'oa_plan__view_'+selected.id
						, modal: false
					}).max();	
					
			        top.$.ligerui.get("oa_plan__view_"+selected.id).close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__view_"+selected.id));
			        };
				}
				
			}else if(permission[i].code=='500111'){
				//导入
				theFunction = oa_plan.upload;
			}else if(permission[i].code=='500112'){
				//导出
				theFunction = oa_plan.download;
			}else if(permission[i].code=='500121'){
				//添加
				theFunction = function(){		
			        	
					top.$.ligerDialog.open({ 
						 url: 'oa_plan__add.html'
						,height: 530
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "oa_plan__add"
						, modal: false
						,title: getIl8n("oa_plan","resident")+getIl8n("add")
					});	
					
			        top.$.ligerui.get("oa_plan__add").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__add"));
			        };					
				}
			}else if(permission[i].code=='500122'){
				//修改 	                					
				theFunction = function(){
	            	if(top.$.ligerui.get("oa_plan__modify")){
	            		alert("close first");return;
	            	}else{
						var selected = oa_plan.grid_getSelectOne();
	            		var id = selected.id;
	            	}					
					
					top.$.ligerDialog.open({ 
						 url: 'oa_plan__modify.html?id='+id+"&for=bar"
						,height: 400
						,width: 400
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						,id: "oa_plan__modify"
						, modal: false
					});	
					
			        top.$.ligerui.get("oa_plan__modify").close = function(){
			            var g = this;
			            top.$.ligerui.win.removeTask(this);
			            g.unmask();
			            g._removeDialog();
			            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__modify"));
			        };						
				}		
			}else if(permission[i].code=='500123'){
				//删除
				theFunction = oa_plan.remove;
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
		if($.ligerui.get('oa_plan__grid').options.checkbox){
			selected = $.ligerui.get('oa_plan__grid').getSelecteds();
			if(selected.length!=1){ 
				alert(getIl8n("selectOne") );
				return;
			}
			selected = selected[0];
		}else{
			selected = $.ligerui.get('oa_plan__grid').getSelected();
			if(selected==null){
				alert(getIl8n("selectOne"));
				return;
			}
		}	
		return selected;
	}

	
	,searchOptions: {}
	,gantt: function(hasMenu){
		if(!hasMenu){
			$('body').append("<div id='menu'></div>");
			
			//配置列表表头的按钮,根据当前用户的权限来初始化
			var config = {
				toolbar: {
					items: []
				}
			};
			var permission = [];
			for(var i=0;i<top.basic_user.permission.length;i++){
				if(top.basic_user.permission[i].code=='50'){
					permission = top.basic_user.permission[i].children;
					for(var j=0;j<permission.length;j++){
						if(permission[j].code=='5001'){
							permission = permission[j].children;
						}
					}				
				}
			}
			for(var i=0;i<permission.length;i++){
				var theFunction = function(){};
				
				if(permission[i].code=='500101'){
					theFunction = oa_plan.search;
				}else if(permission[i].code=='500111'){
					theFunction = oa_plan.upload;
				}else if(permission[i].code=='500112'){
					theFunction = oa_plan.download;
				}else if(permission[i].code=='500123'){
					theFunction = oa_plan.remove;
				}else if(permission[i].code=='500122'){
					theFunction =  function(){	
						var selected = oa_plan.ganttSelected;
						
						top.$.ligerDialog.open({ 
							url: 'oa_plan__modify.html?id='+selected.id+'&random='+Math.random()
							,height: 500
							,width: 650
							,title: selected.name
							,isHidden: false
							,id: 'oa_plan__modify_'+selected.id
						});	
						
				        top.$.ligerui.get("oa_plan__modify_"+selected.id).close = function(){
				            var g = this;
				            top.$.ligerui.win.removeTask(this);
				            g.unmask();
				            g._removeDialog();
				            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__modify_"+selected.id));
				        };
					}
				}else if(permission[i].code=='500121'){
					theFunction =  function(){	
						var win = top.$.ligerDialog.open({ 
							url: 'oa_plan__add.html?random='+Math.random()
							,height: 500
							,width: 650
							,title: getIl8n("add")
							,isHidden: false
							, showMax: true
							, showToggle: true
							, showMin: true						
							, modal: false
							,id: "oa_plan__add"
						});
													
				        win.close = function(){
				            var g = this;
				            top.$.ligerui.win.removeTask(this);
				            g.unmask();
				            g._removeDialog();
				            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__add"));
				        };						
					}

				}else if(permission[i].code=='500102'){
					theFunction =  function(){	
						var selected = oa_plan.ganttSelected;
	                    	
                    	var id = selected.id;
                        if(top.$.ligerui.get("win__oa_plan__view_"+id)){
                            top.$.ligerui.get("win__oa_plan__view_"+id).show();
                            return;
                        }
                        top.$.ligerDialog.open({
                            isHidden:false,
                            id : "win__oa_plan__view_"+id 
                            ,height: 500
                            ,width: 650
                            ,url: "oa_plan__view.html?id="+id  
                            ,showMax: true
                            ,showToggle: true
                            ,showMin: true
                            ,isResize: true
                            ,modal: false
                            ,title: selected.name
                            ,slide: false    
                        });
                        
				        top.$.ligerui.get("win__oa_plan__view_"+selected.id).close = function(){
				            var g = this;
				            top.$.ligerui.win.removeTask(this);
				            g.unmask();
				            g._removeDialog();
				            top.$.ligerui.remove(top.$.ligerui.get("win__oa_plan__view_"+selected.id));
				        };
					}
				}else if(permission[i].code=='500190'){
					theFunction =  function(){  	
						var selected = oa_plan.ganttSelected;						
						top.$.ligerDialog.open({ 
							 url: 'oa_plan__examine.html?id='+selected.id+'&random='+Math.random()
							,height: 380
							,width: 720
							,title: selected.username
							,isResize: true
							,isHidden: false
						});	
					}
				}else if(permission[i].code=='500191'){
					theFunction = oa_plan.track;
				}else if(permission[i].code=='500192'){
					theFunction = oa_plan.statistics_dialog;
				}
				config.toolbar.items.push({line: true });
					config.toolbar.items.push({
						text: permission[i].name , img:permission[i].icon , click : theFunction
					});
			}
			
			$("#menu").ligerToolBar({ items: config.toolbar.items });
			$('body').append("<div id='gantt'></div>");
		}
		$("#gantt").gantt({
			source: config_path__oa_plan__gantt
			,serverData: {
				 search: $.ligerui.toJSON(oa_plan.searchOptions)
				 
                ,executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
                ,pagesize: 18
                ,page: 1
			},	
			navigate: "scroll",
			scale: "weeks",
			maxScale: "months",
			minScale: "days",
			itemsPerPage: 18,
			onItemClick: function(objData) {
				if(oa_plan.ganttSelected){
					$('#cell_'+oa_plan.ganttSelected.id).removeAttr('style');
				}
				var id = objData.id;
				$('#cell_'+id).css('background-color','red');
				oa_plan.ganttSelected = objData;
			}
		});
	}
	,ganttSelected: null
	
	,track: function(){
		$("#gantt").empty();
		$("#gantt").gantt({
			source: config_path__oa_plan__gantt
			,serverData: {
				 search: $.ligerui.toJSON(oa_plan.searchOptions)
				 
                ,executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
                ,pagesize: 18
                ,page: 1
			},	
			navigate: "scroll",
			scale: "weeks",
			maxScale: "months",
			minScale: "days",
			itemsPerPage: 18,
			onItemClick: function(objData) {
				if(oa_plan.ganttSelected){
					$('#cell_'+oa_plan.ganttSelected.id).removeAttr('style');
				}
				var id = objData.id;
				$('#cell_'+id).css('background-color','red');
				oa_plan.ganttSelected = objData;
			}
		});
	} 
	
	,remove: function(){
		var selected = oa_plan.ganttSelected;
		if(selected==null){
			console.debug(oa_plan.ganttSelected);
		}

		if(confirm( top.getIl8n('sureToDelete') )){
			var ids = selected.id;		
			
			$.ajax({
				url: config_path__oa_plan__remove,
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
						$.ligerui.get('oa_plan__grid').loadData();
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
	
	,add_group_weight: function(){
		var win = top.$.ligerui.get("group_weight");
		if(win){			
			top.$.ligerui.win.addTask(win);
			win.show();	
		}else{
			win = top.$.ligerDialog.open({ 
				  id : "group_weight"
				  
				, height: 500
				, url: "oa_plan__group_weight.html?rand="+Math.random()
				, width: 750
				
				, isHidden: true 
				, showMax: true
				, showToggle: true
				, showMin: true
				, isResize: true
				, modal: false
				, title: top.getIl8n('oa_plan','weight') 
				, slide: false			
			});		
		}
		win.hide = function(){
			var groups = "";
			var weights = "";
			for(i=0;i<top.myglobal.group_weight__data.length;i++){
				groups += top.myglobal.group_weight__data[i].code+",";
				weights += top.myglobal.group_weight__data[i].weight+",";
			}
			if(top.myglobal.group_weight__data.length!=0){
				groups = groups.substring(0,groups.length-1);		
				weights = weights.substring(0,weights.length-1);				
			}
			$.ligerui.get("groups_participate").setValue(groups);
			$.ligerui.get("groups_weight").setValue(weights);
			top.$.ligerui.win.removeTask(this);
			this._hideDialog();			
		};
	}
	
	,add_quotes_weight: function(){
		var win = top.$.ligerui.get("win__add_quotes_weight");
		if(win){			
			top.$.ligerui.win.addTask(win);
			win.show();	
		}else{
			win = top.$.ligerDialog.open({ 
				  id : "win__add_quotes_weight"
				  
				, height: 500
				, url: "oa_plan__quotes_weight.html?rand="+Math.random()
				, width: 750
				
				, isHidden: true 
				, showMax: true
				, showToggle: true
				, showMin: true
				, isResize: true
				, modal: false
				, title: top.getIl8n('oa_plan','weight') 
				, slide: false			
			});		
		}
		win.hide = function(){
			var quotess = "";
			var weights = "";
			for(i=0;i<top.myglobal.quotes_weight__data.length;i++){
				quotess += top.myglobal.quotes_weight__data[i].code+",";
				weights += top.myglobal.quotes_weight__data[i].weight+",";
			}
			if(top.myglobal.quotes_weight__data.length!=0){
				quotess = quotess.substring(0,quotess.length-1);		
				weights = weights.substring(0,weights.length-1);				
			}
			$.ligerui.get("quotes").setValue(quotess);
			$.ligerui.get("quotes_weight").setValue(weights);
			top.$.ligerui.win.removeTask(this);
			this._hideDialog();			
		};
	}	
	
	,upload: function(){		

		top.$.ligerDialog.open({ 
			 content: "<iframe id='oa_plan_upload_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+
			 	config_path__oa_plan__upload+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+
			 	" target='send'><input name='file' type='file' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form>"
			,height: 250
			,width: 400
			,isHidden: false
			,id: "oa_plan__upload"
		});
		
		top.$.ligerui.get("oa_plan__upload").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__upload"));
        };			

		top.$("#oa_plan_upload_if").load(function(){
	        var d = top.$("#oa_plan_upload_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
	        	oa_plan.gantt(1);
	        }else{
	        	alert(obj.msg);
	        }
	    }); 
	}	
	
	,download: function(){
		var data = {
			search: $.ligerui.toJSON(oa_plan.searchOptions)
			,pagesize: 200
			,page: 1
            ,executor: top.basic_user.loginData.username
			,session: top.basic_user.loginData.session
		};
		
		$.ajax({
			 url: config_path__oa_plan__download
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
					,id: "oa_plan__download"
				});
				
				top.$.ligerui.get("oa_plan__download").close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__download"));
		        };		
			    		
			},
			error : function(){
				
				alert(top.getIl8n('disConnect'));
			}
		});	
	}
	
	,addContent: function(){
		var win = top.$.ligerui.get("win_addContent");
		if(win){			
			top.$.ligerui.win.addTask(win);
			win.show();
			var iframeDom = top.$.ligerui.get("win_addContent").frame;	
			iframeDom.set($.ligerui.get('content').getValue());			
		}else{
			win = top.$.ligerDialog.open({ 
				  id : "win_addContent"
				  
				, height: 500
				, url: "oa_plan__add_content.html?rand="+Math.random()
				, width: 750
				
				, isHidden: true 
				, showMax: true
				, showToggle: true
				, showMin: true
				, isResize: true
				, modal: false
				, title: top.getIl8n('oa_plan','plan') +" "+ top.getIl8n('oa_plan','content') 
				, slide: false
				, buttons:[{
					text: top.getIl8n('save'), onclick: function(){
						 var iframeDom = top.$.ligerui.get("win_addContent").frame;	
						 iframeDom.save();
						 $.ligerui.get('content').setValue(top.myglobal.FCKCcontent);
					}
				}]				
			});		
		}
		win.hide = function(){
			top.$.ligerui.win.removeTask(this);
			this._hideDialog();
		};
	}		
	
	,checkCode: function(){
		$.ajax({
			 url: config_path__oa_plan__checkCode
			,data: $('#requirement').val()
			,type: "POST"
			,dataType: 'json'

			,success: function(response) {
				if(response.status==1){
					alert("OK");
				}else{
					alert("Wrong Code");
				}			    		
			},
			error : function(){				
				alert(top.getIl8n('disConnect'));
			}
		});	
	}
	
	,usergrid: function(){
		var config = {
				id: 'oa_plan__usergrid'
				,height:'100%'
				,columns: [
				     { display: getIl8n("name"), name: 'name', width: 200 }
				    ,{ display: getIl8n("basic_user","username"), name: 'username', width:120, hide:true }
				    ,{ display: getIl8n("basic_user","group_code"), name: 'group_code', hide:true }
				    ,{ display: getIl8n("basic_user","group"), name: 'groupname' , width:220 }
		    
				],  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
	                ,search: "{}"
				},
				url: config_path__oa_plan__usergrid,
				method: "POST",				
				toolbar: { items: [{
					text: getIl8n("search") , click: function(){
						$.ligerui.get("oa_plan__usergrid").options.parms.search= $.ligerui.toJSON({"name":$('#search_name').val()});
						$.ligerui.get("oa_plan__usergrid").loadData();
					}
				},{
					text: getIl8n("clear") , click: function(){
						$.ligerui.get("oa_plan__usergrid").options.parms.search= "{}";
						$.ligerui.get("oa_plan__usergrid").loadData();
					}
				},{
					text: getIl8n("oa_plan","getUser") , click: function(){
						var selected = $.ligerui.get('oa_plan__usergrid').getSelected();
						top.myglobal.searchUserName = selected.username;
						top.myglobal.searchUserGroupCode = selected.group_code;
					}
				}]}
		};
		
		$(document.body).ligerGrid(config);
		$('.l-toolbar').prepend("<input type='text' id='search_name' class='l-text' style='float: left;' />");
	}
	
	,group_weight: function(){
		$('body').append("<table style='width:100%'><tr><td id='left' style='width:50%'></td><td id='right' style='width:50%'></td></tr></table>");
		var config = {
				id: 'group_right'
				,height:'100%'
				,width: 450
				,columns: [
				     { display: getIl8n("name"), name: 'name', width: 150 }
				    ,{ display: getIl8n("code"), name: 'code', width: 150 ,hide:true }
				    
				],  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
	                ,search: "{}"
				},
				url: config_path__oa_plan__groupgrid,
				method: "POST",				
				toolbar: { items: [{
					text: getIl8n("search") , click: function(){
						$.ligerui.get("group_right").options.parms.search= $.ligerui.toJSON({"name":$('#search_name').val()});
						$.ligerui.get("group_right").loadData();
					}
				},{
					text: getIl8n("oa_plan","weight") , click: function(){
						var selected = $.ligerui.get('group_right').getSelected();
						var weight=prompt(getIl8n("oa_plan","weight"),"100");
						if (weight!=null && weight!=""){
							var rowData = selected;
							rowData.weight = weight;
							$.ligerui.get("group_left").addRow(rowData);
						}						
					}
				}]}
		};
		
		$("#right").ligerGrid(config);
		var config2 = {
				id: 'group_left'
				,height:'100%'
				,width: 500
				
				,columns: [
				     { display: getIl8n("name"), name: 'name' , width:150 }
				    ,{ display: getIl8n("code"), name: 'code' , width:150 ,hide:true  }
				    ,{ display: getIl8n("oa_plan","weight"), name: 'weight' , width:150  }
				],  pageSize:20 ,rownumbers:true
				,toolbar: { items: [{
					text: getIl8n("ok") , click: function(){
						top.myglobal.group_weight__data = $.ligerui.get("group_left").getData();
					}
				},{
					text: getIl8n("clear") , click: function(){
						$.ligerui.get("group_left").loadData(null);
						top.myglobal.group_weight__data = [];
					}
				}]}
		};
		
		$("#left").ligerGrid(config2);
		$('.l-panel-bbar-inner').remove();
		$($('.l-toolbar')[1]).prepend("<input type='text' id='search_name' class='l-text' style='float: left;' />");	
	}
	
	,quotes_weight: function(){
		$('body').append("<table style='width:100%'><tr><td id='left' style='width:50%'></td><td id='right' style='width:50%'></td></tr></table>");
		var config = {
				id: 'quotes_right'
				,height:'100%'
				,width: 450
				,columns: [
				     { display: getIl8n("name"), name: 'name', width: 150 }
				    ,{ display: getIl8n("code"), name: 'code', width: 150 ,hide:true }
				    
				],  pageSize:20 ,rownumbers:true
				,parms : {
	                executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session     
	                ,search: "{}"
				},
				url: config_path__oa_plan__quotesgrid,
				method: "POST",				
				toolbar: { items: [{
					text: getIl8n("search") , click: function(){
						$.ligerui.get("quotes_right").options.parms.search= $.ligerui.toJSON({"name":$('#search_name').val()});
						$.ligerui.get("quotes_right").loadData();
					}
				},{
					text: getIl8n("oa_plan","weight") , click: function(){
						var selected = $.ligerui.get('quotes_right').getSelected();
						var weight=prompt(getIl8n("oa_plan","weight"),"100");
						if (weight!=null && weight!=""){
							var rowData = selected;
							rowData.weight = weight;
							$.ligerui.get("quotes_left").addRow(rowData);
						}						
					}
				}]}
		};
		
		$("#right").ligerGrid(config);
		var config2 = {
				id: 'quotes_left'
				,height:'100%'
				,width: 500
				
				,columns: [
				     { display: getIl8n("name"), name: 'name' , width:150 }
				    ,{ display: getIl8n("code"), name: 'code' , width:150 ,hide:true  }
				    ,{ display: getIl8n("oa_plan","weight"), name: 'weight' , width:150  }
				],  pageSize:20 ,rownumbers:true
				,toolbar: { items: [{
					text: getIl8n("ok") , click: function(){
						top.myglobal.quotes_weight__data = $.ligerui.get("quotes_left").getData();
					}
				},{
					text: getIl8n("clear") , click: function(){
						$.ligerui.get("quotes_left").loadData(null);
						top.myglobal.quotes_weight__data = [];
					}
				}]}
		};
		
		$("#left").ligerGrid(config2);
		$('.l-panel-bbar-inner').remove();
		$($('.l-toolbar')[1]).prepend("<input type='text' id='search_name' class='l-text' style='float: left;' />");	
	}	
	
	,get_user_incharge: function(){
		var win = top.$.ligerui.get("win_userSearch");
		if(win){			
			top.$.ligerui.win.addTask(win);
			win.show();	
		}else{
			win = top.$.ligerDialog.open({ 
				  id : "win_userSearch"
				
				, title: getIl8n("oa_plan","getUser")  
				, height: 500
				, url: "oa_plan__usergrid.html?rand="+Math.random()
				, width: 500
				
				, isHidden: true 
				, showMax: true
				, showToggle: true
				, showMin: true
				, isResize: true
				, modal: false
				, slide: false			
			});		
		}
		win.hide = function(){
			top.$.ligerui.win.removeTask(this);
			this._hideDialog();
			$.ligerui.get("user_incharge").setValue(top.myglobal.searchUserName);
			$.ligerui.get("group_incharge").setValue(top.myglobal.searchUserGroupCode);
		};
	}
	
	,get_theUp: function(){
		var win = top.$.ligerui.get("win_5001").frame;
		var code = win.oa_plan.ganttSelected.code;
		$.ligerui.get("theUp").setValue(code);
	}
		
	,add: function(){
		
		var config = {
			id: 'oa_plan__add',
			fields: [
				 { display: top.getIl8n('oa_plan','theUp'), name: "theUp", type: "text" , width: 490 }
				 			
				,{ display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }							
				,{ display: top.getIl8n('oa_plan','content'), name: "content", type: "text", validate: { required:true }, newline: false }
				,{ display: top.getIl8n('oa_plan','files'), name: "files", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('oa_plan','requirement'), name: "requirement", type: "text" , newline: false}
				,{ display: top.getIl8n('type'), name: "oa_plan__type", type: "select", options :{data : oa_plan.config.oa_plan__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('oa_plan','plan_output'), name: "plan_output", type: "text", validate: { required:true,number: true } , newline: false}
											
				,{ display: top.getIl8n('oa_plan','plan_time_start'), name: "plan_time_start", type: "date", validate: { required:true } , group: top.getIl8n('oa_plan','input')}
				,{ display: top.getIl8n('oa_plan','plan_time_stop'), name: "plan_time_stop", type: "date", validate: { required:true }  , newline: false}
				,{ display: top.getIl8n('oa_plan','plan_personhour'), name: "plan_personhour", type: "text", validate: { required:true ,number: true}}
				,{ display: top.getIl8n('oa_plan','plan_money'), name: "plan_money", type: "text", validate: { required:true,number: true }  , newline: false}

				,{ display: top.getIl8n('oa_plan','group_incharge'), name: "group_incharge", type: "text", validate: { required:true } ,group: top.getIl8n('oa_plan','participate')}
				,{ display: top.getIl8n('oa_plan','user_incharge'), name: "user_incharge", type: "text", validate: { required:true }, newline: false }

			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#requirement").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.checkCode()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		
		$("#content").attr("disabled",true);
		$("#content").css("background-color","black");
		$("#content").parent().css("background-color","black");
		$("#content").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.addContent()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");		
		
		$("#theUp").attr("disabled",true);
		$("#theUp").css("background-color","#EEEEEE");
		$("#theUp").parent().css("background-color","#EEEEEE");
		$("#theUp").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.get_theUp()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		
		$("#files").attr("disabled",true);
		$("#files").css("background-color","#EEEEEE");
		$("#files").parent().css("background-color","#EEEEEE");		
		$("#files").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.upload()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		
		$("#quotes").attr("disabled",true);
		$("#quotes").css("background-color","#EEEEEE");
		$("#quotes").parent().css("background-color","#EEEEEE");	
		$("#quotes").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.setWeight()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
				
		$("#quotes_weight").attr("disabled",true);
		$("#quotes_weight").css("background-color","#EEEEEE");
		$("#quotes_weight").parent().css("background-color","#EEEEEE");		
		
		$("#user_incharge").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.get_user_incharge()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
								
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="oa_plan__submit" class="l-button l-button-submit" />' );

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
				$("#oa_plan__submit").attr("value",top.getIl8n('waitting'));				
				
				$.ajax({
					url: config_path__oa_plan__add,
					data: {
		                 executor: top.basic_user.loginData.username		                 
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 "theUp":$.ligerui.get('theUp').getValue()
							,"name":$.ligerui.get('name').getValue()
							,"content":$.ligerui.get('content').getValue()
							,"files":$.ligerui.get('files').getValue()
							,"requirement":$.ligerui.get('requirement').getValue()
							,"type":$.ligerui.get('oa_plan__type').getValue()
							,"plan_output":$.ligerui.get('plan_output').getValue()
							
							,"plan_time_start":$('#plan_time_start').val()
							,"plan_time_stop":$('#plan_time_stop').val()
							,"plan_personhour":$.ligerui.get('plan_personhour').getValue()
							,"plan_money":$.ligerui.get('plan_money').getValue()
							
							,"group_incharge":$.ligerui.get('group_incharge').getValue()
							,"user_incharge":$.ligerui.get('user_incharge').getValue()
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#oa_plan__submit").attr("value", top.getIl8n('submit') );
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#oa_plan__submit").remove();
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
	}	
	
	,modify: function(){
		var config = {
			id: 'oa_plan__modify',
			fields: [
	
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }							
				,{ display: top.getIl8n('oa_plan','content'), name: "content", type: "text", validate: { required:true }, newline: false }
				,{ display: top.getIl8n('oa_plan','files'), name: "files", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('oa_plan','requirement'), name: "requirement", type: "text" , newline: false}
				,{ display: top.getIl8n('type'), name: "type", type: "select", options :{data : oa_plan.config.oa_plan__type, valueField : "code" , textField: "value", slide: false }, validate: {required:true} }
				,{ display: top.getIl8n('oa_plan','plan_output'), name: "plan_output", type: "text", validate: { required:true,number: true } , newline: false}
											
				,{ display: top.getIl8n('oa_plan','plan_time_start'), name: "plan_time_start", type: "date", validate: { required:true } , group: top.getIl8n('oa_plan','input')}
				,{ display: top.getIl8n('oa_plan','plan_time_stop'), name: "plan_time_stop", type: "date", validate: { required:true }  , newline: false}
				,{ display: top.getIl8n('oa_plan','plan_personhour'), name: "plan_personhour", type: "text", validate: { required:true ,number: true}}
				,{ display: top.getIl8n('oa_plan','plan_money'), name: "plan_money", type: "text", validate: { required:true,number: true }  , newline: false}

				,{ display: top.getIl8n('oa_plan','group_incharge'), name: "group_incharge", type: "text", validate: { required:true } ,group: top.getIl8n('oa_plan','participate')}
				,{ display: top.getIl8n('oa_plan','user_incharge'), name: "user_incharge", type: "text", validate: { required:true }, newline: false }

			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#requirement").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.checkCode()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		
		$("#content").attr("disabled",true);
		$("#content").css("background-color","black");
		$("#content").parent().css("background-color","black");
		$("#content").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.addContent()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");		
		
		$("#files").attr("disabled",true);
		$("#files").css("background-color","#EEEEEE");
		$("#files").parent().css("background-color","#EEEEEE");		
		$("#files").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.upload()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		
		$("#user_incharge").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.get_user_incharge()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
								
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="oa_plan__submit" class="l-button l-button-submit" />' );

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
				$("#oa_plan__submit").attr("value",top.getIl8n('waitting'));

				$.ajax({
					url: config_path__oa_plan__modify,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							 id: getParameter("id", window.location.toString() )
							
							,"name":$.ligerui.get('name').getValue()
							,"content":$.ligerui.get('content').getValue()
							,"files":$.ligerui.get('files').getValue()
							,"requirement":$.ligerui.get('requirement').getValue()
							,"type":$.ligerui.get('type').getValue()
							,"plan_output":$.ligerui.get('plan_output').getValue()
							
							,"plan_time_start":$('#plan_time_start').val()
							,"plan_time_stop":$('#plan_time_stop').val()
							,"plan_personhour":$.ligerui.get('plan_personhour').getValue()
							,"plan_money":$.ligerui.get('plan_money').getValue()
							
							,"group_incharge":$.ligerui.get('group_incharge').getValue()
							,"user_incharge":$.ligerui.get('user_incharge').getValue()
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#oa_plan__submit").attr("value", top.getIl8n('submit') );
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#oa_plan__submit").remove();
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
		
		$.ajax({
			url: config_path__oa_plan__view
			,data: {
				id: getParameter("id", window.location.toString() )
				
				,executor: top.basic_user.loginData.username
				,session: top.basic_user.loginData.session
			}
			,type: "POST"
			,dataType: 'json'						
			,success: function(response) {	
			    var data = response.data;
				
				$.ligerui.get('name').setValue(data.name);
				$.ligerui.get('content').setValue(data.content);
				$.ligerui.get('files').setValue(data.files);
				$.ligerui.get('requirement').setValue(data.requirement);
				$.ligerui.get('type').setValue(data.type);
				$.ligerui.get('plan_output').setValue(data.plan_output);
				$.ligerui.get('plan_time_start').setValue(data.plan_time_start);
				$.ligerui.get('plan_time_stop').setValue(data.plan_time_stop);
				$.ligerui.get('plan_personhour').setValue(data.plan_personhour);
				$.ligerui.get('plan_money').setValue(data.plan_money);
				$.ligerui.get('group_incharge').setValue(data.group_incharge);
				$.ligerui.get('user_incharge').setValue(data.user_incharge);

			}
		});
	}
	
	//AJAX 通信状态,如果为TRUE,则表示服务端还在通信中	
	,ajaxState: false 	
	,examine: function(){
			
		var config = {
			id: 'oa_plan__examine',
			fields: [
				 { display: top.getIl8n('name'), name: "name", type: "text", validate: { required:true } }	
				,{ display: top.getIl8n('status'), name: "status", type: "select", options :{data : oa_plan.config.oa_plan__status, valueField : "code" , textField: "value", slide: false }, validate: {required:true}, newline: false }
				,{ display: top.getIl8n('oa_plan','result_time_start'), name: "result_time_start", type: "date", validate: { required:true } }
				,{ display: top.getIl8n('oa_plan','result_time_stop'), name: "result_time_stop", type: "date", validate: { required:true }  , newline: false}
				,{ display: top.getIl8n('oa_plan','result_personhour'), name: "result_personhour", type: "text", validate: { required:true ,number: true}}
				,{ display: top.getIl8n('oa_plan','result_money'), name: "result_money", type: "text", validate: { required:true,number: true }  , newline: false}				

				,{ display: top.getIl8n('oa_plan','groups_participate'), name: "groups_participate", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('oa_plan','weight'), name: "groups_weight", type: "text", validate: { required:true }, newline: false }
				,{ display: top.getIl8n('oa_plan','quotes'), name: "quotes", type: "text", validate: { required:true } }
				,{ display: top.getIl8n('oa_plan','weight'), name: "quotes_weight", type: "text", validate: { required:true }, newline: false }				
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#name").attr("disabled",true);
		$("#name").css("background-color","#EEEEEE");
		$("#name").parent().css("background-color","#EEEEEE");
		
		$("#quotes").attr("disabled",true);
		$("#quotes").css("background-color","#EEEEEE");
		$("#quotes").parent().css("background-color","#EEEEEE");	
		$("#quotes_weight").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.add_quotes_weight()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$("#quotes_weight").attr("disabled",true);
		$("#quotes_weight").css("background-color","#EEEEEE");
		$("#quotes_weight").parent().css("background-color","#EEEEEE");		
		
		$("#groups_participate").attr("disabled",true);
		$("#groups_participate").css("background-color","#EEEEEE");
		$("#groups_participate").parent().css("background-color","#EEEEEE");	
		$("#groups_weight").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_plan.add_group_weight()'  ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$("#groups_weight").attr("disabled",true);
		$("#groups_weight").css("background-color","#EEEEEE");
		$("#groups_weight").parent().css("background-color","#EEEEEE");			
							
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('submit')+'" id="oa_plan__submit" class="l-button l-button-submit" />' );

		var v = $('#form').validate({
			debug: true,
			errorPlacement: function (lable, element) {
				if (element.hasClass("l-text-field")) {
					element.parent().modifyClass("l-text-invalid");
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
				$("#oa_plan__submit").attr("value",top.getIl8n('waitting'));
				
				$.ajax({
					url: config_path__oa_plan__examine,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON({
							id: getParameter("id", window.location.toString() )
							
							,"status": $.ligerui.get('status').getValue()
							,"result_time_start": $('#result_time_start').val()
							,"result_time_stop": $('#result_time_stop').val()
							,"result_personhour": $.ligerui.get('result_personhour').getValue()
							,"result_money": $.ligerui.get('result_money').getValue()
							,"groups_participate": $.ligerui.get('groups_participate').getValue()
							,"groups_weight": $.ligerui.get('groups_weight').getValue()
							,"quotes": $.ligerui.get('quotes').getValue()
							,"quotes_weight": $.ligerui.get('quotes_weight').getValue()
						})
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#oa_plan__submit").attr("value", top.getIl8n('submit') );
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#oa_plan__submit").remove();
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
			url: config_path__oa_plan__view
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
							
				$.ligerui.get("name").setValue(data.name);	
				//TODO
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

					 { display: top.getIl8n('oa_plan','group_incharge'), name: "oa_plan__search_group", newline: true, type: "text" }
					,{ display: top.getIl8n('name'), name: "oa_plan__search_name", newline: true, type: "text" }
					,{ display: top.getIl8n('oa_plan','plan_time_start'), name: "plan_time_plan_time_start", type: "date", newline: true }
					,{ display: top.getIl8n('oa_plan','plan_time_stop'), name: "plan_time_plan_time_stop", type: "date", newline: true }
					
					,{ display: top.getIl8n('type'), name: "oa_plan__search_type", newline: true, type: "select", options :{data : oa_plan.config.oa_plan__type, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('status'), name: "oa_plan__search_status", newline: true, type: "select", options :{data : oa_plan.config.oa_plan__status , valueField : "code" , textField: "value" } }
					
				]
			}); 
			$.ligerDialog.open({
				 id: "formD"
				,width: 350
				,height: 250
				,content: form
				,title: top.getIl8n('search')
				,buttons : [
				    //清空查询条件
					{text: top.getIl8n('basic_user','clear'), onclick:function(){
						oa_plan.searchOptions = {};
						oa_plan.gantt(true);
						
						$.ligerui.get("oa_plan__search_type").setValue('');
						$.ligerui.get("oa_plan__search_zone_10").setValue('');
						$.ligerui.get("oa_plan__search_status").setValue('');
						$.ligerui.get("oa_plan__search_name").setValue('');
					}},
					//提交查询条件
				    {text: top.getIl8n('basic_user','search'), onclick:function(){
						var data = {};
						var  name =		$.ligerui.get("oa_plan__search_name").getValue()
						 	,type = 		$.ligerui.get("oa_plan__search_type").getValue()
						 	,group_incharge = 		$.ligerui.get("oa_plan__search_group").getValue()
						 	,status = 		$.ligerui.get("oa_plan__search_status").getValue()
						 	,plan_time_start = 		$("#plan_time_plan_time_start").val()
						 	,plan_time_stop = 		$("#plan_time_plan_time_stop").val()
						 	;
						
						if(name!="")data.name = name;
						if(type!="")data.type = type;
						if(group_incharge!="")data.group_incharge = group_incharge;
						if(status!="")data.status = status;
						if(plan_time_start!="")data.plan_time_start = plan_time_start;
						if(plan_time_stop!="")data.plan_time_stop = plan_time_stop;
						
						oa_plan.searchOptions = data;
						oa_plan.gantt(true);
				}}]
			});
		}
	}
	
	,viewData: {}
	,view: function(){
		var id = getParameter("id", window.location.toString() );
    	
    	var htmls = "";
    	$.ajax({
            url: config_path__oa_plan__view
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
            	
            	oa_plan.viewData = response.data;
            	for(var j in data){   
            		if(j=='id'||j=='creater_code'||j=='updater_code'||j=='creater_group_code'||j=='time_created'||j=='type'||j=='status')continue;
            		if(j=='id'||j=='remark'||j=='content')htmls+="<div style='width:100%;float:left;display:block;margin-top:5px;'/>";
            		            		
            		if(j=='remark'||j=='types_'||j=='job'||j=='content'||j=='name'||j=='code'){
	            		eval("var key = getIl8n('oa_plan','"+j+"');");
	            		htmls += "<span class='view_lable' style='width:95%'>"+key+"</span><span style='width:95%' class='view_data'>"+data[j]+"</span>";
            		}else{
            			eval("var key = getIl8n('oa_plan','"+j+"');");
                		htmls += "<span class='view_lable'>"+key+"</span><span class='view_data'>"+data[j]+"</span>";
            		}
            	}; 
            	$(document.body).html("<div id='menu'  ></div><div id='data'></div>");
            	$("#data").html(htmls);
            	            	
            	//查看详细,页面上也有按钮的
            	var items = [];            	
                var permission = top.basic_user.permission;
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='50'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }      
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='5001'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }   
                for(var i=0;i<permission.length;i++){
                    if(permission[i].code=='500102'){
                    	if(typeof(permission[i].children)=='undefined')return;
                        permission = permission[i].children;
                        break;
                    }
                }            
                
                for(var i=0;i<permission.length;i++){        
                	var theFunction = function(){};
                    if(permission[i].code=='50010220'){
                    	//列表
                        theFunction = function(){
        					top.$.ligerDialog.open({ 
	       						 url: "oa_work__grid.html?search={plan:'"+data.code+"'}&f=b"
	       						,height: 500
	       						,width: 700
	       						,isHidden: false
	       						, showMax: true
	       						, showToggle: true
	       						, showMin: true	
	       						, title: data.name
	       						,id: "oa_work__grid"+data.code
	       						, modal: false
	       					});	
	       					
	       			        top.$.ligerui.get("oa_work__grid"+data.code).close = function(){
	       			            var g = this;
	       			            top.$.ligerui.win.removeTask(this);
	       			            g.unmask();
	       			            g._removeDialog();
	       			            top.$.ligerui.remove(top.$.ligerui.get("oa_work__grid"+data.code));
	       			        };						
	       				};
                    }else if(permission[i].code=='50010222'){
                        theFunction = function(){};
                    }else if(permission[i].code=='50010223'){
                        theFunction = function(){};
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
						 {"id":"GIS","text": top.getIl8n('GIS')},
						 {"id":"table","text": top.getIl8n('table')} 
					 ]} }
					,{ display: top.getIl8n('type'), name: "statistics_dialog__type", type: "select", options :{data : [
						{"id":"time","text":top.getIl8n('time')},
						{"id":"attribute","text":top.getIl8n('attribute')},
						{"id":"code","text":top.getIl8n('code')}
					] } }					 
					,{ display: top.getIl8n('attribute'), name: "statistics_dialog__attribute", type: "select", options :{data :[
						 {"id":"type",text: top.getIl8n('type')}
						,{"id":"status",text: top.getIl8n('status')}
						
					] } }
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
						var search = $.ligerui.toJSON(oa_plan.searchOptions);
						if(mode=='chart'){
							var type = $.ligerui.get('statistics_dialog__type').getValue();
							if(type=='time'){
								
								var win = top.$.ligerDialog.open({ 
									 url: 'oa_plan__statistics.html?type='+type+'&mode='+mode+'&attribute='+attribute+'&search='+search+'&rand='+Math.random()
									, height: 250
									, width: 400
									, isHidden: false
									, showMax: true
									, showToggle: true
									, showMin: true						
									, modal: false
									, id: "oa_plan__statistics_time"
									, title: top.getIl8n('statistics')
								});
								
								win.max();
								
								win.close = function(){
						            var g = this;
						            top.$.ligerui.win.removeTask(this);
						            g.unmask();
						            g._removeDialog();
						            top.$.ligerui.remove(top.$.ligerui.get("oa_plan__statistics_time"));
						        };
							}							
						}
					}}]
			});	
		}
	}
	
	,statistics: function(){
		var type = getParameter("type", window.location.toString() );
		var mode = getParameter("mode", window.location.toString() );
		var attribute = getParameter("attribute", window.location.toString() );
		var search = getParameter("search", window.location.toString() );
		
		if(type=='time'){
			$.ajax({
				url: config_path__oa_plan__statistics_time
				,data: {					
					 search: search
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
				url: config_path__oa_plan__statistics_attribute
				,data: {					
					 search: search
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
	}
};