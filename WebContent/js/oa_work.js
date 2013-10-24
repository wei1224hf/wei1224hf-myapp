
var oa_work = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__oa_work__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				oa_work.config = response;
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
			id: 'oa_work__grid'
			,height:'100%'
			,pageSizeOptions: [10, 20, 30, 40, 50 ,2000]
			,columns: [
			     { display: getIl8n("id"), name: 'id', isSort: true, hide:true }
			    ,{ display: getIl8n("oa_work","plan"), name: 'plan_', width: 100,align: 'left' }
			    ,{ display: getIl8n("oa_work","time"), name: 'time', width: 100 }
			    ,{ display: getIl8n("oa_work","title"), name: 'title', width:120,align: 'left' }
			    ,{ display: getIl8n("oa_work","hour"), name: 'hour', width: 80 }
			    ,{ display: getIl8n("creater_code"), name: 'creater_code', width: 80 }
			    ,{ display: getIl8n("creater_group_code"), name: 'creater_group_code', width: 80 }
			    ,{ display: getIl8n("oa_work","businesstype"), name: 'businesstype_', width: 50 }				    
			    ,{ display: getIl8n("status"), name: 'status_', width: 50 }
			    ,{ display: getIl8n("type"), name: 'type_', width: 100 }
		    
			],  pageSize:20 ,rownumbers:true
			,parms : {
                executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session     
               
			},
			url: config_path__oa_work__grid,
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
			if(top.basic_user.permission[i].code=='50'){
				permission = top.basic_user.permission[i].children;
				for(var j=0;j<permission.length;j++){
					if(permission[j].code=='5002'){
						permission = permission[j].children;
					}
				}				
			}
		}
		
		for(var i=0;i<permission.length;i++){
			var theFunction = null;
			if(permission[i].code=='500201'){
				//查询
				theFunction = oa_work.search;
			}else if(permission[i].code=='500211'){
				//导入
				theFunction = oa_work.upload;
			}else if(permission[i].code=='500212'){
				//导出
				theFunction = oa_work.download;
			}else if(permission[i].code=='500223'){
				//删除
				theFunction = oa_work.remove;
				config.checkbox = true;
			}else if(permission[i].code=='500290'){
				//审批
				theFunction = oa_work.examine;
				config.checkbox = true;
			}else if(permission[i].code=='500292'){
				//统计
				theFunction = oa_work.statistics_dialog;
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
		if($.ligerui.get('oa_work__grid').options.checkbox){
			selected = $.ligerui.get('oa_work__grid').getSelecteds();
			if(selected.length!=1){ 
				alert(getIl8n("selectOne") );
				return;
			}
			selected = selected[0];
		}else{
			selected = $.ligerui.get('oa_work__grid').getSelected();
			if(selected==null){
				alert(getIl8n("selectOne"));
				return;
			}
		}	
		return selected;
	}
	
	,remove: function(){
		var selected = $.ligerui.get('oa_work__grid').getSelecteds();
		if(selected.length==0){alert(top.getIl8n('noSelect'));return;}
		if(confirm( top.getIl8n('sureToDelete') )){
			var ids = "";
			for(var i=0; i<selected.length; i++){
				ids += selected[i].id+",";
			}
			ids = ids.substring(0,ids.length-1);				
			
			$.ajax({
				url: config_path__oa_work__remove,
				data: {
					ids: ids 
					
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					if(response.status=="1"){
						$.ligerui.get('oa_work__grid').loadData();
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
	
	,examine: function(){
		var selected = $.ligerui.get('oa_work__grid').getSelecteds();
		if(selected.length==0){alert(top.getIl8n('noSelect'));return;}
		if(confirm( top.getIl8n('areYouSure') )){
			var ids = "";
			for(var i=0; i<selected.length; i++){
				ids += selected[i].id+",";
			}
			ids = ids.substring(0,ids.length-1);				
			
			$.ajax({
				url: config_path__oa_work__examine,
				data: {
					ids: ids 
					
	                ,executor: top.basic_user.loginData.username
	                ,session: top.basic_user.loginData.session
				}
				,type: "POST"
				,dataType: 'json'
				,success: function(response) {
					if(response.status=="1"){
						$.ligerui.get('oa_work__grid').loadData();
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
						,{"id":"creater_code",text: getIl8n("creater_code")}
						,{"id":"creater_group_code",text: getIl8n("creater_group_code")}
					] } }
					
				]
			}); 

			$.ligerDialog.open({
				 id: "dialog__statistics"
				,width: 450
				,height: 350
				,content: form
				,title: top.getIl8n('statistics')
				,buttons : [
					{text: top.getIl8n('ok'), onclick:function(){
						var mode = $.ligerui.get('statistics_dialog__mode').getValue();
						var type = $.ligerui.get('statistics_dialog__type').getValue();
						var attribute = $.ligerui.get('statistics_dialog__attribute').getValue();
						var search = $.ligerui.toJSON( $.ligerui.get("oa_work__grid").options.parms.search );
						if(mode=='chart'){
							var type = $.ligerui.get('statistics_dialog__type').getValue();
								
							var win = top.$.ligerDialog.open({ 
								 url: 'oa_work__statistics.html?type='+type+'&mode='+mode+'&attribute='+attribute+'&search='+search+'&rand='+Math.random()
								, height: 250
								, width: 400
								, isHidden: false
								, showMax: true
								, showToggle: true
								, showMin: true						
								, modal: false
								, id: "oa_work__statistics_time"
								, title: top.getIl8n('statistics')
							});
							
							win.max();
							
							win.close = function(){
					            var g = this;
					            top.$.ligerui.win.removeTask(this);
					            g.unmask();
					            g._removeDialog();
					            top.$.ligerui.remove(top.$.ligerui.get("oa_work__statistics_time"));
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
 						,{"id":"creater_code",text: getIl8n("creater_code")}
						,{"id":"creater_group_code",text: getIl8n("creater_group_code")} 						
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
				url: config_path__oa_work__statistics_time
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
			                name: 'Count',
			                data: response.series
			            },{
			                name: 'Sum',
			                data: response.series2
			            }]
		        	});
		        	
		        	$('text:last').remove();
				}
			});
		}
		
		if(type=='attribute'){
			$.ajax({
				url: config_path__oa_work__statistics_attribute
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
				    };
					if(attribute=="creater_code"||attribute=="creater_group_code"){
						config = {
				            chart: {
				                type: 'bar',
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
				                name: 'Count',
				                data: response.series
				            },{
				                name: 'Sum',
				                data: response.series2
				            }]
			        	};
					}

					$('body').highcharts(config);
		        	
		        	$('text:last').remove();
				}
			});
		}		
	}	
	
	,upload: function(){		
		var path = $('#photo').val();
		
		top.$.ligerDialog.open({ 
			 content: "<iframe id='oa_work_upload_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+
			 	config_path__oa_work__upload+"&executor="+top.basic_user.loginData.username+"&session="+top.basic_user.loginData.session+
			 	" target='send'><input name='file' type='file' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form>"
			,height: 250
			,width: 400
			,isHidden: false
			,id: "oa_work__upload"
		});
		
		top.$.ligerui.get("oa_work__upload").close = function(){
            var g = this;
            top.$.ligerui.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            top.$.ligerui.remove(top.$.ligerui.get("oa_work__upload"));
        };			

		top.$("#oa_work_upload_if").load(function(){
	        var d = top.$("#oa_work_upload_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){
				alert(obj.msg);
	        }
	    }); 
	}	
	
	,download: function(){

		var data = $.ligerui.get('oa_work__grid').options.parms;
		data.pagesize = $.ligerui.get('oa_work__grid').options.pageSize;
		data.page = $.ligerui.get('oa_work__grid').options.page;
		
		
		$.ajax({
			 url: config_path__oa_work__download
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
					,id: "oa_work__download"
				});
				
				top.$.ligerui.get("oa_work__download").close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("oa_work__download"));
		        };		
			    		
			},
			error : function(){
				//net error,则删除按钮再也不能点了
				alert(top.getIl8n('disConnect'));
			}
		});	
	}
	
	//AJAX 通信状态,如果为TRUE,则表示服务端还在通信中	
	,ajaxState: false 	
	
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
					 { display: top.getIl8n('type'), name: "oa_work__search_type", newline: true, type: "select", options :{data : oa_work.config.oa_work__type, valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('status'), name: "oa_work__search_status", newline: true, type: "select", options :{data : oa_work.config.oa_work__status , valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('title'), name: "oa_work__search_title", newline: true, type: "text" }
					,{ display: top.getIl8n('time_start'), name: "time_start", type: "date" }		
					,{ display: top.getIl8n('time_stop'), name: "time_stop", type: "date" }				
					,{ display: top.getIl8n('creater_code'), name: "creater_code", type: "text" }			
					,{ display: top.getIl8n('oa_work','plan'), name: "plan", type: "text" }						
					,{ display: top.getIl8n('creater_group_code'), name: "creater_group_code", type: "text" }						
				]
			}); 
			$.ligerDialog.open({
				 id: "formD"
				,width: 350
				,height: 300
				,content: form
				,title: top.getIl8n('search')
				,buttons : [
				    //清空查询条件
					{text: top.getIl8n('basic_user','clear'), onclick:function(){
						$.ligerui.get("oa_work__grid").options.parms.search = "{}";
						$.ligerui.get("oa_work__grid").loadData();
						
						$.ligerui.get("oa_work__search_title").setValue('');
						$.ligerui.get("oa_work__search_type").setValue('');
						$.ligerui.get("oa_work__search_status").setValue('');
						$.ligerui.get("time_start").setValue('');
						$.ligerui.get("time_stop").setValue('');
						$.ligerui.get("creater_code").setValue('');
						$.ligerui.get("creater_group_code").setValue('');
						$.ligerui.get("plan").setValue('');
					}},
					//提交查询条件
				    {text: top.getIl8n('basic_user','search'), onclick:function(){
						var data = {};
						var  title =		$.ligerui.get("oa_work__search_title").getValue()
						 	,type = 		$.ligerui.get("oa_work__search_type").getValue()
						 	,status = 		$.ligerui.get("oa_work__search_status").getValue()
						 	,time_start = 		$("#time_start").val()
						 	,time_stop = 		$("#time_stop").val()
						 	,creater_code = 		$.ligerui.get("creater_code").getValue()
						 	,creater_group_code = 		$.ligerui.get("creater_group_code").getValue()
						 	,plan = 		$.ligerui.get("plan").getValue()
						 	;
						
						if(title!="")data.title = title;
						if(type!="")data.type = type;
						if(status!="")data.status = status;
						if(time_start!="")data.time_start = time_start;
						if(time_stop!="")data.time_stop = time_stop;
						if(creater_code!="")data.creater_code = creater_code;
						if(creater_group_code!="")data.creater_group_code = creater_group_code;
						if(plan!="")data.plan = plan;
						
						$.ligerui.get("oa_work__grid").options.parms.search= $.ligerui.toJSON(data);
						$.ligerui.get("oa_work__grid").loadData();
				}}]
			});
		}
	}
};