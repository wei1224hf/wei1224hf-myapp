
var government_company = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__government_company__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				government_company.config = response;
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
			id: 'government_company__grid'
			,height:'100%'
			,pageSizeOptions: [10, 20, 30, 40, 50 ,2000]
			,columns: [
			       { display: getIl8n("id"), name: 'id', isSort: true, hide:true }
		      
			      ,{ display: getIl8n('government_company','code'), name: 'code', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','name'), name: 'name', width: 100 }
			      ,{ display: getIl8n('government_company','code2'), name: 'code2', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','tax'), name: 'tax', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','business'), name: 'business', width: 120 }
			      ,{ display: getIl8n('government_company','socialworker'), name: 'socialworker', width: 100 }
			      ,{ display: getIl8n('government_company','time_in'), name: 'time_in', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','time_out'), name: 'time_out', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','type'), name: 'type', width: 100 }
			      ,{ display: getIl8n('government_company','status'), name: 'status', width: 100 }
			      ,{ display: getIl8n('government_company','count_employee'), name: 'count_employee', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','property'), name: 'property', width: 100, hide:true }
			      ,{ display: getIl8n('government_company','turnover '), name: 'turnover ', width: 100, hide:true }

			],  pageSize:20 ,rownumbers:true
			,parms : {
                executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session     
               
			},
			url: config_path__government_company__grid,
			method: "POST"
			,toolbar: { items: []}
		};		
		
		var search = getParameter("search", window.location.toString() );
		if(search!=""){
			config.parms.search = search;
		}else{
			config.parms.search = "{}";
		}			
		
		var permission = [];
		for(var i=0;i<top.basic_user.permission.length;i++){
			if(top.basic_user.permission[i].code=='52'){
				permission = top.basic_user.permission[i].children;
				for(var j=0;j<permission.length;j++){
					if(permission[j].code=='5204'){
						permission = permission[j].children;
					}
				}				
			}
		}
		
		for(var i=0;i<permission.length;i++){
			var theFunction = null;
			if(permission[i].code=='520401'){
				//查询
				theFunction = government_company.search;
			}else if(permission[i].code=='520411'){
				//导入
				theFunction = government_company.upload;
			}else if(permission[i].code=='520412'){
				//导出
				theFunction = government_company.download;
			}else if(permission[i].code=='520423'){
				//删除
				theFunction = government_company.remove;
				config.checkbox = true;
			}else if(permission[i].code=='520421'){
				//添加
				theFunction = function(){		
			        	
					var win = top.$.ligerDialog.open({ 
						 url: 'government_company__add.html'
						,height: 500
						,width: 960
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						, title: getIl8n("government_company","socialworker") + " "+ getIl8n("add")
						,id: "government_company__add"
						, modal: false
					});	
					
					win.close = function(){
			            top.$.ligerui.win.removeTask(this);
			            this.unmask();
			            this._removeDialog();
			            top.$.ligerui.remove(this);
			        };					
				};
			}else if(permission[i].code=='520422'){
				//修改
				theFunction = function(){		
					var id;
	            	if(top.$.ligerui.get("government_company__modify")){
	            		alert("close first");return;
	            	}else{
						var selected = government_company.grid_getSelectOne();
	            		id = selected.id;
	            	}	
	            	
					var win = top.$.ligerDialog.open({ 
						 url: 'government_company__modify.html?id='+id+"&f=b"
						,height: 600 
						,width: 960
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true	
						, title: getIl8n("government_company","socialworker") + " "+ getIl8n("modify")
						,id: "government_company__modify"
						, modal: false
					});	
					
					win.close = function(){
			            top.$.ligerui.win.removeTask(this);
			            this.unmask();
			            this._removeDialog();
			            top.$.ligerui.remove(this);
			        };					
				};
			}else if(permission[i].code=='520402'){
				//查看
				theFunction = function(){
					var selected = government_company.grid_getSelectOne();
	            	var id = selected.id;
	                if(top.$.ligerui.get("government_company__view_"+id)){
	                    top.$.ligerui.get("government_company__view_"+id).show();
	                    return;
	                }					
					var win = top.$.ligerDialog.open({ 
						url: 'government_company__view.html?id='+selected.id+'&random='+Math.random()
						,height: 450
						,width: 800
						,title: selected.name
						,isHidden: false
						, showMax: true
						, showToggle: true
						, showMin: true						
						,id: 'government_companying__view_'+selected.id
						, modal: false
					});	
					
					//win.max();
					
					win.close = function(){		            
			            top.$.ligerui.win.removeTask(this);
			            this.unmask();
			            this._removeDialog();
			            top.$.ligerui.remove(this);
			        };
				};
			}else if(permission[i].code=='520492'){
				//统计
				theFunction = government_company.statistics_dialog;
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
		if($.ligerui.get('government_company__grid').options.checkbox){
			selected = $.ligerui.get('government_company__grid').getSelecteds();
			if(selected.length!=1){ 
				alert(getIl8n("selectOne") );
				return;
			}
			selected = selected[0];
		}else{
			selected = $.ligerui.get('government_company__grid').getSelected();
			if(selected==null){
				alert(getIl8n("selectOne"));
				return;
			}
		}	
		return selected;
	}
	
	,remove: function(){
		var selected = $.ligerui.get('government_company__grid').getSelecteds();
		if(selected.length==0){alert(top.getIl8n('noSelect'));return;}
		if(confirm( top.getIl8n('sureToDelete') )){
			var ids = "";
			for(var i=0; i<selected.length; i++){
				ids += selected[i].id+",";
			}
			ids = ids.substring(0,ids.length-1);				
			
			$.ajax({
				url: config_path__government_company__remove,
				data: {
					ids: ids 
					
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					if(response.status=="1"){
						$.ligerui.get('government_company__grid').loadData();
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
						var search = $.ligerui.toJSON( $.ligerui.get("government_company__grid").options.parms.search );
						if(mode=='chart'){
							var type = $.ligerui.get('statistics_dialog__type').getValue();
								
							var win = top.$.ligerDialog.open({ 
								 url: 'government_company__statistics.html?type='+type+'&mode='+mode+'&attribute='+attribute+'&search='+search+'&rand='+Math.random()
								, height: 250
								, width: 400
								, isHidden: false
								, showMax: true
								, showToggle: true
								, showMin: true						
								, modal: false
								, id: "government_company__statistics_time"
								, title: top.getIl8n('statistics')
							});
							
							win.max();
							
							win.close = function(){
					            var g = this;
					            top.$.ligerui.win.removeTask(this);
					            g.unmask();
					            g._removeDialog();
					            top.$.ligerui.remove(top.$.ligerui.get("government_company__statistics_time"));
					        };
						}							
						
					}}]
			});	
			
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
					]);
				}
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
				url: config_path__government_company__statistics_time
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
				url: config_path__government_company__statistics_attribute
				,data: {					
					 search: search
					,executor: top.basic_user.loginData.username
					,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'						
				,success: function(response) {		
					console.debug(response);
					$('body').highcharts({
				        chart: {
				            plotBackgroundColor: null,
				            plotBorderWidth: null,
				            plotShadow: false
				        },
				        title: {
				            text: 'Browser market shares at a specific website, 2010'
				        },
				        tooltip: {
				    	    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
				        },
				        plotOptions: {
				            pie: {
				                allowPointSelect: true,
				                cursor: 'pointer',
				                dataLabels: {
				                    enabled: true,
				                    color: '#000000',
				                    connectorColor: '#000000',
				                    format: '<b>{point.name}</b>: {point.percentage:.1f} %'
				                }
				            }
				        },
				        series: [{
				            type: 'pie',
				            name: 'Browser share',
				            data: response.data
				        }]
				    });
		        	
		        	$('text:last').remove()
				}
			});
		}
	}	
	
	,upload: function(){		
		var path = $('#photo').val();
		
		top.$.ligerDialog.open({ 
			 content: "<iframe id='government_company_upload_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+
			 	config_path__government_company__upload+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+
			 	" target='send'><input name='file' type='file' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form>"
			,height: 250
			,width: 400
			,isHidden: false
			,id: "government_company__upload"
		});
		
		top.$.ligerui.get("government_company__upload").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("government_company__upload"));
        };			

		top.$("#government_company_upload_if").load(function(){
	        var d = top.$("#government_company_upload_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
				alert(obj.msg);
	        }
	    }); 
	}	
	
	,download: function(){

		var data = $.ligerui.get('government_company__grid').options.parms;
		data.pagesize = $.ligerui.get('government_company__grid').options.pageSize;
		data.page = $.ligerui.get('government_company__grid').options.page;
		
		
		$.ajax({
			 url: config_path__government_company__download
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
					,id: "government_company__download"
				});
				
				top.$.ligerui.get("government_company__download").close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_company__download"));
		        };		
			    		
			},
			error : function(){
				//net error,则删除按钮再也不能点了
				alert(top.getIl8n('disConnect'));
			}
		});	
	}
	
	,ajaxState: false 	
	,searchOptions: {}	
	,search: function(){
		var search_dialog;
		if($.ligerui.get("search_dialog")){
			search_dialog = $.ligerui.get("search_dialog");
			search_dialog.show();
		}else{
			var form = $("<form id='search_from'></form>");
			$(form).ligerForm({
				inputWidth: 170
				,labelWidth: 90
				,space: 40
				,fields: [
					 { display: top.getIl8n('type'), name: "search___government_company__type", newline: true, type: "select", options :{data : government_company.config.
						government_company__type, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('status'), name: "search___government_company__status", newline: true, type: "select", options :{data : government_company.config.
						government_company__status, valueField : "code" , textField: "value" }, newline: false }
					,{ display: top.getIl8n('government_company','type_working'), name: "search___government_company__type_working", newline: true, type: "select", options :{data : government_company.config.
						government_company__type_working, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('government_company','type_employ'), name: "search___government_company__type_employ", newline: true, type: "select", options :{data : government_company.config.
						government_company__type_employ, valueField : "code" , textField: "value" }, newline: false }
					,{ display: top.getIl8n('types'), name: "search___government_company__types", newline: true, type: "select", options :{data : government_company.config.
						government_company__types, valueField : "code" , textField: "value", isMultiSelect: true } }
					,{ display: top.getIl8n('government_company','expert_gruops'), name: "search___government_company__expert_gruops", newline: true, type: "select", options :{data : government_company.config.
						government_company__expert_gruops, valueField : "code" , textField: "value", isMultiSelect: true  }, newline: false }		

					,{ display: top.getIl8n('government_company','time_work_here'), name: "search___government_company__time_work_here_big", type: "date" }
					,{ display: top.getIl8n('government_company','time_work_here'), name: "search___government_company__time_work_here_small", type: "date", newline: false }
					,{ display: top.getIl8n('government_company','age_work_community'), name: "search___government_company__age_work_community_big", newline: true, type: "number" }
					,{ display: top.getIl8n('government_company','age_work_community'), name: "search___government_company__age_work_community_small", newline: true, type: "number", newline: false }

					,{ display: top.getIl8n('oa_person','birthday'), name: "search___oa_person__birthday_big", type: "date", group: "&nbsp;" }		
					,{ display: top.getIl8n('oa_person','birthday'), name: "search___oa_person__birthday_small", type: "date", newline: false }	
					,{ display: top.getIl8n('oa_person','name'), name: "search___oa_person__name", newline: true, type: "text"}
					,{ display: top.getIl8n('oa_person','gender'), name: "search___oa_person__gender", newline: true, type: "select", options :{data : government_company.config.
						oa_person__gender, valueField : "code" , textField: "value" }, newline: false }		
					,{ display: top.getIl8n('oa_person','marriage'), name: "search___oa_person__marriage", newline: true, type: "select", options :{data : government_company.config.
						oa_person__marriage, valueField : "code" , textField: "value" } }		
					,{ display: top.getIl8n('oa_person','nation'), name: "search___oa_person__nation", newline: true, type: "select", options :{data : government_company.config.
						oa_person__nation, valueField : "code" , textField: "value" }, newline: false }		
					,{ display: top.getIl8n('oa_person','politically'), name: "search___oa_person__politically", newline: true, type: "select", options :{data : government_company.config.
						oa_person__politically, valueField : "code" , textField: "value" } }		
					,{ display: top.getIl8n('oa_person','degree'), name: "search___oa_person__degree", newline: true, type: "select", options :{data : government_company.config.
						oa_person__degree, valueField : "code" , textField: "value" }, newline: false  }		
				]
			}); 
			$.ligerDialog.open({
				 id: "search_dialog"
				,width: 650
				,height: 400
				,content: form
				,title: top.getIl8n('search')
				,buttons : [
				    //清空查询条件
					{text: top.getIl8n('basic_user','clear'), onclick:function(){
						$.ligerui.get("government_company__grid").options.parms.search = "{}";
						$.ligerui.get("government_company__grid").loadData();

						var doms = $("input[type='text']",$('#search_from'));
						for(var i=0;i<doms.length;i++){
							var theid = $(doms[i]).attr('id');
							$.ligerui.get(theid).setValue("");
						}
					}},
					//提交查询条件
				    {text: top.getIl8n('basic_user','search'), onclick:function(){
						var data = {};
						
						var doms = $("input[type='text']",$('#search_from'));
						for(var i=0;i<doms.length;i++){
							var theid = $(doms[i]).attr('id');
							var thekey = theid.replace('search___',"");
							var thetype = $(doms[i]).attr('ltype');							
						
							var thevalue = $.ligerui.get(theid).getValue();
							if(thetype=='date')thevalue = $('#'+theid).val();
							if(thevalue!="" && thevalue!=0 && thevalue!="0" && thevalue!=null){
								eval("data."+thekey+"='"+thevalue+"'");
							}
						}
						
						$.ligerui.get("government_company__grid").options.parms.search= $.ligerui.toJSON(data);
						$.ligerui.get("government_company__grid").loadData();
				}}]
			});
			
			$("#search___government_company__time_work_here_big").parent().parent().parent().next().append("&nbsp;"+ top.getIl8n('big'));
			$("#search___government_company__time_work_here_small").parent().parent().parent().next().append("&nbsp;"+ top.getIl8n('small'));
			$("#search___government_company__age_work_community_big").parent().parent().next().append("&nbsp;"+ top.getIl8n('big'));
			$("#search___government_company__age_work_community_small").parent().parent().next().append("&nbsp;"+ top.getIl8n('small'));	
			$("#search___oa_person__birthday_big").parent().parent().parent().next().append("&nbsp;"+ top.getIl8n('big'));
			$("#search___oa_person__birthday_small").parent().parent().parent().next().append("&nbsp;"+ top.getIl8n('small'));			
		}
	}
	
	,add: function(){

		var config = {
			id: 'government_company__add',
			fields: [

				 { display: top.getIl8n('basic_user','username'), name: 'basic_user__username', type: 'text', validate: { required: true }  } 
				,{ display: top.getIl8n('basic_user','group'), name: 'basic_user__group_code', type: 'select', validate: { required: true },options :{data : government_company.config.
					department, valueField : "code" , textField: "value" } , newline: false } 
				,{ display: top.getIl8n('government_company','id_person'), name: 'government_company__id_person', type: 'text', validate: { required: false } , newline: false } 
				
				,{ display: top.getIl8n('government_company','age_work'), name: 'government_company__age_work', type: 'number', validate: { required: false } , group: '&nbsp;' } 
				,{ display: top.getIl8n('government_company','age_work_community'), name: 'government_company__age_work_community', type: 'number', validate: { required: false } , newline: false } 
				,{ display: top.getIl8n('government_company','time_work'), name: 'government_company__time_work', type: 'date', validate: { required: false }, newline: false } 
				,{ display: top.getIl8n('government_company','time_work_community'), name: 'government_company__time_work_community', type: 'date', validate: { required: false }  } 
				,{ display: top.getIl8n('government_company','time_work_here'), name: 'government_company__time_work_here', type: 'date', validate: { required: false }, newline: false } 
				,{ display: top.getIl8n('government_company','type_working'), name: 'government_company__type_working', type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__type_working, valueField : "code" , textField: "value" } , newline: false } 
				,{ display: top.getIl8n('government_company','type_employ'), name: 'government_company__type_employ',  type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__type_employ, valueField : "code" , textField: "value" }}  
				,{ display: top.getIl8n('government_company','log_work_change'), name: 'government_company__log_work_change', type: 'text', validate: { required: false } , newline: false} 
				,{ display: top.getIl8n('government_company','log_work_rewards'), name: 'government_company__log_work_rewards', type: 'text', validate: { required: false }  } 
				,{ display: top.getIl8n('government_company','types'), name: 'government_company__types',  type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__types, valueField : "code" , textField: "value", isMultiSelect: true } , newline: false } 
				,{ display: top.getIl8n('government_company','expert_gruops'), name: 'government_company__expert_gruops',  type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__expert_gruops, valueField : "code" , textField: "value", isMultiSelect: true }  , newline: false } 
				,{ display: top.getIl8n('government_company','description'), name: 'government_company__description', type: 'text', validate: { required: false } } 
				,{ display: top.getIl8n('government_company','type'), name: 'government_company__type',  type: 'select', validate: { required: true },options :{data : government_company.config.
					government_company__type, valueField : "code" , textField: "value" }  } 

			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#government_company__id_person").attr("disabled",true);
		$("#government_company__id_person").css("background-color","#EEEEEE");
		$("#government_company__id_person").parent().css("background-color","#EEEEEE");		
		$("#government_company__id_person").parent().parent().next().append("&nbsp;<a href='#' onclick='government_company.addPerson()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('add')+'" id="government_company__submit" class="l-button l-button-submit" />' );
		
		/*
		 TODO
		$.ligerui.get('zone_10').setValue('2102041303');
		$.ligerui.get('name').setValue('住房楼宇');
		$.ligerui.get('owner').setValue('张三');
		$.ligerui.get('time_founded').setValue('1960-01-01');
		$.ligerui.get('time_over').setValue('2050-01-01');
		$.ligerui.get('income').setValue('10000');
		$.ligerui.get('count_member').setValue('7');		
		$.ligerui.get('government_company__type').setValue('1');
		$.ligerui.get('government_company__types').setValue('1;2');
		$.ligerui.get('government_company__status').setValue('10');
		*/
		
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
				$("#government_company__submit").attr("value",top.getIl8n('waitting'));
				
				var data = {};
				var doms = $("input[type='text']",$('#form'));
				for(var i=0;i<doms.length;i++){
					var theid = $(doms[i]).attr('id');
					var thetype = $(doms[i]).attr('ltype');							
				
					var thevalue = $.ligerui.get(theid).getValue();
					if(thetype=='date')thevalue = $('#'+theid).val();
					if(thevalue!="" && thevalue!=0 && thevalue!="0" && thevalue!=null){
						eval("data."+theid+"='"+thevalue+"'");
					}
				}
				data.government_company__id_person = $('#government_company__id_person').val();
				
				$.ajax({
					url: config_path__government_company__add,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session
		                
						,data: $.ligerui.toJSON(data)
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							government_company.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#government_company__submit").remove();
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_company__submit").attr("value", top.getIl8n('submit') );
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
	}	
	
	
	,ajaxState: false
	,modify: function(){
		
		var config = {
			id: 'government_company__modify',
			fields: [

				 { display: top.getIl8n('basic_user','username'), name: 'basic_user__username', type: 'text', validate: { required: true }  } 
				,{ display: top.getIl8n('basic_user','group'), name: 'basic_user__group_code', type: 'select', validate: { required: true },options :{data : government_company.config.
					department, valueField : "code" , textField: "value" } , newline: false } 
				,{ display: top.getIl8n('government_company','id_person'), name: 'government_company__id_person', type: 'text', validate: { required: false } , newline: false } 
				
				,{ display: top.getIl8n('government_company','age_work'), name: 'government_company__age_work', type: 'number', validate: { required: false } , group: '&nbsp;' } 
				,{ display: top.getIl8n('government_company','age_work_community'), name: 'government_company__age_work_community', type: 'number', validate: { required: false } , newline: false } 
				,{ display: top.getIl8n('government_company','time_work'), name: 'government_company__time_work', type: 'date', validate: { required: false }, newline: false } 
				,{ display: top.getIl8n('government_company','time_work_community'), name: 'government_company__time_work_community', type: 'date', validate: { required: false }  } 
				,{ display: top.getIl8n('government_company','time_work_here'), name: 'government_company__time_work_here', type: 'date', validate: { required: false }, newline: false } 
				,{ display: top.getIl8n('government_company','type_working'), name: 'government_company__type_working', type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__type_working, valueField : "code" , textField: "value" } , newline: false } 
				,{ display: top.getIl8n('government_company','type_employ'), name: 'government_company__type_employ',  type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__type_employ, valueField : "code" , textField: "value" }} 
				,{ display: top.getIl8n('government_company','duties'), name: 'government_company__duties', type: 'text', validate: { required: false }, newline: false  } 
				,{ display: top.getIl8n('government_company','log_work_change'), name: 'government_company__log_work_change', type: 'text', validate: { required: false } , newline: false} 
				,{ display: top.getIl8n('government_company','log_work_rewards'), name: 'government_company__log_work_rewards', type: 'text', validate: { required: false }  } 
				,{ display: top.getIl8n('government_company','types'), name: 'government_company__types',  type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__types, valueField : "code" , textField: "value", isMultiSelect: true } , newline: false } 
				,{ display: top.getIl8n('government_company','expert_gruops'), name: 'government_company__expert_gruops',  type: 'select', validate: { required: false },options :{data : government_company.config.
					government_company__expert_gruops, valueField : "code" , textField: "value", isMultiSelect: true }  , newline: false } 
				,{ display: top.getIl8n('government_company','description'), name: 'government_company__description', type: 'text', validate: { required: false } } 
				,{ display: top.getIl8n('type'), name: 'government_company__type',  type: 'select', validate: { required: true },options :{data : government_company.config.
					government_company__type, valueField : "code" , textField: "value" } , newline: false } 
				,{ display: top.getIl8n('status'), name: 'government_company__status',  type: 'select', validate: { required: true },options :{data : government_company.config.
					government_company__status, valueField : "code" , textField: "value" }, newline: false  } 				

			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#government_company__id_person").attr("disabled",true);
		$("#government_company__id_person").css("background-color","#EEEEEE");
		$("#government_company__id_person").parent().css("background-color","#EEEEEE");		
		$("#government_company__id_person").parent().parent().next().append("&nbsp;<a href='#' onclick='government_company.addPerson()' ><div class='form_dilog_tip' >&nbsp;</div></a>");	
		
		$("#government_company__duties").attr("disabled",true);
		$("#government_company__duties").css("background-color","#EEEEEE");
		$("#government_company__duties").parent().css("background-color","#EEEEEE");		
		$("#government_company__duties").parent().parent().next().append("&nbsp;<a href='#' onclick='government_company.duty_select()' ><div class='form_dilog_tip' >&nbsp;</div></a>");	
		
		
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('modify')+'" id="government_company__submit" class="l-button l-button-submit" />' );
			
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
				if(government_company.ajaxState)return;
				government_company.ajaxState = true;
				$("#government_company__submit").attr("value",top.getIl8n('waitting'));
				
				var data = {};
				var doms = $("input[type='text']",$('#form'));
				for(var i=0;i<doms.length;i++){
					var theid = $(doms[i]).attr('id');
					var thetype = $(doms[i]).attr('ltype');							
				
					var thevalue = $.ligerui.get(theid).getValue();
					if(thetype=='date')thevalue = $('#'+theid).val();
					if(thevalue!="" && thevalue!=0 && thevalue!="0" && thevalue!=null){
						eval("data."+theid+"='"+thevalue+"'");
					}
				}
				data.government_company__id_person = $('#government_company__id_person').val();		
				data.id = getParameter("id", window.location.toString() );
				
				$.ajax({
					url: config_path__government_company__modify,
					data: {
		                 executor: top.basic_user.loginData.username
		                ,session: top.basic_user.loginData.session		                
						,data: $.ligerui.toJSON(data)
					},
					type: "POST",
					dataType: 'json',						
					success: function(response) {		
						//服务端添加成功,修改 AJAX 通信状态,修改按钮的文字信息,读取反馈信息
						if(response.status=="1"){
							basic_user.ajaxState = false;
							alert(top.getIl8n('done'));
							$("#government_company__submit").remove();
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#government_company__submit").attr("value", top.getIl8n('submit') );
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
			url: config_path__government_company__view
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
			    for (x in data) {
			    	if(x.substring(x.length-1,x.length)=="_"){
			    		eval("delete data."+x+";");
			    	}
			    }
			    
			    $.ligerui.get('basic_user__username').setValue(data.username);
			    $('#basic_user__username').attr('disabled',true);
				$("#basic_user__username").css("background-color","#EEEEEE");
				$("#basic_user__username").parent().css("background-color","#EEEEEE");					    
			    $.ligerui.get('basic_user__group_code').setValue(data.group_code);
			    
			    $('#government_company__id_person').val(data.id_person);
			    
			    var tablename = "government_company";
			    for (x in data) {
			    	if(x=="group_code")break;
			    	if(tablename!="government_company")continue;
			    	eval("var thevalue = data."+x+";");
			    	if(x=="types")thevalue = thevalue.replace(",",";");
			    	if(x=="expert_gruops")thevalue = thevalue.replace(",",";");
			    	
			    	$.ligerui.get(tablename+"__"+x).setValue(thevalue);			
			    }			    
			}
		});
	}
	
	,view: function(){

    	$(document.body).html("<div id='menu'  ></div><div id='content' style='width:"+($(window).width()-170)+"px;margin-top:5px;'></div>");
    	var htmls = "";
    	$.ajax({
            url: config_path__government_company__view
            ,data: {
                id: getParameter("id", window.location.toString() ) 
                
                ,executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
            },
            type: "POST",
            dataType: 'json',
            success: function(response) {
            	if(response.status!="1")return;
            	var data = response.data;
			    for (x in data) {
			    	var thex = x.substring(x.length-1,x.length);
			    	if(thex == "_"){
			    		var thexx = x.substring(0,x.length-1);
			    		console.debug(thexx);
			    		eval("data."+thexx+"=data."+x);
			    		eval("delete data."+x);
			    	}		
			    }	            	

	    	
            	for(var j in data){
            		if(j=="id"||j=="creater_code"||j=="creater_group_code"||j=="time_created"||j=="time_lastupdated"||j=="count_updated"||j=="business_code"||j=="tax")continue;

            		
            		if(j=='path_photo'){
        				var str = '<div style="position:absolute;right:5px;top:5px;background-color: rgb(220,250,245);width:166px;height:176px;"><img style="margin:2px;" src="'+data[j]+'" width="160" height="170" /></div>';
        				$("#content").append( str);
        				continue;
            		}
            		
            		if(j=='remark')$("#content").append("<div style='width:100%;float:left;display:block;margin-top:5px;'/>");
	    			eval("var key = getIl8n('government_company','"+j+"');");
	    			$("#content").append( "<span class='view_lable'>"+key+"</span><span class='view_data'>"+data[j]+"</span>");
            	}; 
            	
            	
            }
		});
	}	
	
	
};