var oa_person = {

	 config: null
	,loadConfig: function(afterAjax){
		$.ajax({
			url: config_path__oa_person__loadConfig
			,dataType: 'json'
	        ,type: "POST"
	        ,data: {
                 executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
	        } 			
			,success : function(response) {
				oa_person.config = response;
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
	
	,uploadPhoto: function(){		
		var path = $('#photo').val();
		
		var win = $.ligerDialog.open({ 
			 content: "<iframe id='oa_person_uploadPhoto_if' style='display:none' name='send'><html><body>x</body></html></iframe><form id='xx' method='post' enctype='multipart/form-data' action="+config_path__oa_person__uploadPhoto+" target='send'><input name='file' type='file' /><input name='executor' value='"+top.basic_user.loginData.username+"' style='display:none' /><input name='session' value='"+top.basic_user.loginData.session+"' style='display:none' /><input type='submit' value='"+top.getIl8n('submit')+"' /></form><br/>"
			,height: 150
			,width: 400
			,isHidden: false
			,id: "dialog_photoUpload"
		});
		
		win.close = function(){
            this.unmask();
            this._removeDialog();
			$.ligerui.remove(this);
		};

		$("#oa_person_uploadPhoto_if").load(function(){
	        var d = $("#oa_person_uploadPhoto_if").contents();	        
	        var s = $('body',d).html() ;
	        if(s=='')return;
	        eval("var obj = "+s);
	        if(obj.status=='1'){

	        	$('#photo').val(obj.path);
	        	$('#person_photo__img').attr("src",obj.path);
	        }
	    });  
	}
	
	,getAddressMode: 2
	,getAddress: function(){
		var formD;
		if($.ligerui.get("getAddress_formD")){
			formD = $.ligerui.get("getAddress_formD");
			formD.show();
		}else{
			var form = $("<form id='getAddress_form'></form");
			$(form).ligerForm({
				inputWidth: 170
				,labelWidth: 90
				,space: 40
				,fields: [
					 { display: top.getIl8n('zone_2'), name: "getAddress_zone_2", newline: true, type: "select", options :{data: oa_person.config.zone_2 , valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_4'), name: "getAddress_zone_4", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_6'), name: "getAddress_zone_6", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_8'), name: "getAddress_zone_8", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_10'), name: "getAddress_zone_10", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
				]
				
			}); 
			$.ligerDialog.open({
				 id: "getAddress_formD"
				,width: 350
				,height: 300
				,content: form	
				,title: top.getIl8n('oa_person','address')
				
				,buttons : [
				    {text: top.getIl8n('set'), onclick:function(){
				    	var thecode = "";
				    	var theaddress = "";			
				    	
				    	if($.ligerui.get("getAddress_zone_2").getValue()!=""){
				    		thecode = $('#getAddress_zone_2').val();
				    		theaddress = $('#getAddress_zone_2_val').val();
				    	}
				    	if($.ligerui.get("getAddress_zone_4").getValue()!=""){
				    		thecode = $('#getAddress_zone_4').val();
				    		theaddress = $('#getAddress_zone_4_val').val();
				    	}		
				    	if($.ligerui.get("getAddress_zone_6").getValue()!=""){
				    		thecode = $('#getAddress_zone_6').val();
				    		theaddress = $('#getAddress_zone_6_val').val();
				    	}	
				    	if($.ligerui.get("getAddress_zone_8").getValue()!=""){
				    		thecode = $('#getAddress_zone_8').val();
				    		theaddress += $('#getAddress_zone_8_val').val();
				    	}	
				    	if($.ligerui.get("getAddress_zone_10").getValue()!=""){
				    		thecode = $('#getAddress_zone_10').val();
				    		theaddress += $('#getAddress_zone_10_val').val();
				    	}				    				    					    	
				    			    	
				    	if(oa_person.getAddressMode==1){
							$.ligerui.get("address").setValue(thecode);
							$.ligerui.get("address_code").setValue(theaddress);
				    	}else{
							$.ligerui.get("address_birth").setValue(thecode);
							$.ligerui.get("address_birth_code").setValue(theaddress);
				    	}
					}}]							
			});
			
			$('#getAddress_zone_2').change(function(){
				var data = $('#getAddress_zone_2_val').val();
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
						liger.get("getAddress_zone_4").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});
			
			$('#getAddress_zone_4').change(function(){
				var data = $('#getAddress_zone_4_val').val();
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
						liger.get("getAddress_zone_6").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});		
			
			$('#getAddress_zone_6').change(function(){
				var data = $('#getAddress_zone_6_val').val();
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
						liger.get("getAddress_zone_8").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});	
			
			$('#getAddress_zone_8').change(function(){
				var data = $('#getAddress_zone_8_val').val();
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
						liger.get("getAddress_zone_10").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});										
		}	
	}
	
	,getSchool: function(){		
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
					 { display: top.getIl8n('type'), name: "getSchool_type", newline: false, type: "select", options :{data : [{"code":"3","value":"大学"},{"code":"4","value":"高中"},{"code":"5","value":"初中"},{"code":"6","value":"小学"}], valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_2'), name: "getSchool_zone_2", newline: true, type: "select", options :{data: oa_person.config.zone_2 , valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_4'), name: "getSchool_zone_4", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('zone_6'), name: "getSchool_zone_6", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('py_prefix'), name: "getSchool_prefix", newline: true, type: "select", options :{data : 
					[{"code":"A","value":"A"},{"code":"B","value":"B"},{"code":"C","value":"C"},{"code":"D","value":"D"},{"code":"E","value":"E"},{"code":"F","value":"F"},{"code":"G","value":"G"},{"code":"H","value":"H"},{"code":"I","value":"I"},{"code":"J","value":"J"},{"code":"K","value":"K"},{"code":"L","value":"L"},{"code":"M","value":"M"},{"code":"N","value":"N"},{"code":"O","value":"O"},{"code":"P","value":"P"},{"code":"Q","value":"Q"},{"code":"R","value":"R"},{"code":"S","value":"S"},{"code":"T","value":"T"},{"code":"U","value":"U"},{"code":"V","value":"V"},{"code":"W","value":"W"},{"code":"X","value":"X"},{"code":"Y","value":"Y"},{"code":"Z","value":"Z"}]
					 , valueField : "code" , textField: "value" } }
					,{ display: top.getIl8n('school'), name: "getSchool_school", newline: true, type: "select", options :{valueField : "code" , textField: "value" } }
				]
				
			}); 
			$.ligerDialog.open({
				 id: "formD"
				,width: 350
				,height: 300
				,title: top.getIl8n('school')
				,content: form		
				,buttons : [
					{text: top.getIl8n('read'), onclick:function(){
						var district = $.ligerui.get("getSchool_zone_6").getValue();
						
						$.ajax({
							url: config_path__service_resource__qq_schools
							,dataType: 'json'
					        ,type: "POST"
					        ,data: {
					        	data: $.ligerui.toJSON({
					        		type: $.ligerui.get("getSchool_type").getValue()
					        		,country: "0"
					        		,province: $.ligerui.get("getSchool_zone_2").getValue()
					        		,prefix: $.ligerui.get("getSchool_prefix").getValue()
					        		,district: district
					        	})
					        	
				                ,executor: top.basic_user.loginData.username
				                ,session: top.basic_user.loginData.session
					        } 			
							,success : function(response) {
								liger.get("getSchool_school").setData(response);
							}
							,error : function(){				
								alert(top.il8n.disConnect);
							}
						});
					}},
				    {text: top.getIl8n('set'), onclick:function(){
						$.ligerui.get("degree_school").setValue($('#getSchool_school').val());
						$.ligerui.get("degree_school_code").setValue($('#getSchool_school_val').val());
					}}]		
			});
			
			$('#getSchool_zone_2').change(function(){
				var data = $('#getSchool_zone_2_val').val();
				$.ajax({
					url: config_path__oa_person__lowerCodes,
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
						liger.get("getSchool_zone_4").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});
			
			$('#getSchool_zone_4').change(function(){
				var data = $('#getSchool_zone_4_val').val();
				$.ajax({
					url: config_path__oa_person__lowerCodes,
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
						liger.get("getSchool_zone_6").setData(response);
					},
					error : function(){
						alert(top.getIl8n('disConnect'));
					}
				});	
			});			
		}
	}		

	,add: function(){

		var config = {
			id: 'oa_person__add',
			fields: [
				 { display: top.getIl8n('name'), name: "name", validate: { required:true }, type: "text" }	
				 
				,{ display: top.getIl8n('oa_person','birthday'), name: "birthday", type: "date" }	
				,{ display: top.getIl8n('oa_person','card'), name: "card", type: "select", options :{data : oa_person.config.card, valueField : "code" , textField: "value", slide: false } }	
				,{ display: top.getIl8n('oa_person','cardid'), name: "cardid", type: "text"}		
	
				,{ display: top.getIl8n('oa_person','height'), name: "height", type: "number" }	
				
				,{ display: top.getIl8n('oa_person','cellphone'), name: "cellphone", type: "number" }		
				,{ display: top.getIl8n('oa_person','email'), name: "email", type: "text" }	
				,{ display: top.getIl8n('oa_person','qq'), name: "qq", type: "number", newline: false }		

				,{ display: top.getIl8n('oa_person','nationality'), name: "nationality", type: "text" }					
				
				,{ display: top.getIl8n('oa_person','nation'), name: "nation", type: "select", options :{data : oa_person.config.nation, valueField : "code" , textField: "value", slide: false }, newline: false  }
				,{ display: top.getIl8n('oa_person','gender'), name: "gender", type: "select", options :{data : oa_person.config.gender, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('oa_person','marriage'), name: "marriage", type: "select", options :{data : oa_person.config.marriage, valueField : "code" , textField: "value", slide: false }, newline: false  }
				,{ display: top.getIl8n('oa_person','degree'), name: "degree", type: "select", options :{data : oa_person.config.degree, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('oa_person','politically'), name: "politically", type: "select", options :{data : oa_person.config.politically, valueField : "code" , textField: "value", slide: false }, newline: false  }
				
				,{ display: top.getIl8n('oa_person','photo'), name: "photo", type: "text" }	
				
				,{ display: top.getIl8n('oa_person','degree_school'), name: "degree_school", type: "text" }	
				,{ display: top.getIl8n('oa_person','degree_school_code'), name: "degree_school_code", type: "text" , newline: false}		
				,{ display: top.getIl8n('oa_person','address_birth'), name: "address_birth", type: "text" }	
				,{ display: top.getIl8n('oa_person','address_birth_code'), name: "address_birth_code", type: "text" , newline: false }		
				,{ display: top.getIl8n('oa_person','address'), name: "address", type: "text" }	
				,{ display: top.getIl8n('oa_person','address_code'), name: "address_code", type: "text" , newline: false }
				
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#photo").attr("disabled",true);
		$("#photo").css("background-color","#EEEEEE");
		$("#photo").parent().css("background-color","#EEEEEE");				
		$("#photo").parent().parent().next().css("width","80px").append("&nbsp;<a href='#' onclick='oa_person.uploadPhoto()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$('#form').append("<img class='person_photo' id='person_photo__img' src='../file/defaultphoto.jpg' />");
		
		$("#address_birth_code").attr("disabled",true);
		$("#degree_school_code").attr("disabled",true);
		$("#address_code").attr("disabled",true);
		$("#address_birth_code").css("background-color","#EEEEEE");
		$("#address_birth_code").parent().css("background-color","#EEEEEE");	
		$("#degree_school_code").css("background-color","#EEEEEE");
		$("#degree_school_code").parent().css("background-color","#EEEEEE");	
		$("#address_code").css("background-color","#EEEEEE");
		$("#address_code").parent().css("background-color","#EEEEEE");	
		$("#degree_school").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_person.getSchool()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$("#address_birth").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_person.getAddressMode=2;oa_person.getAddress()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$("#address").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_person.getAddressMode=1;oa_person.getAddress()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
						
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('add')+'" id="oa_person__submit" class="l-button l-button-submit" />' );
		
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
				if(oa_person.ajaxState)return;
				oa_person.ajaxState = true;
				$("#oa_person__submit").attr("value",top.getIl8n('waitting'));
				
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
				
				$.ajax({
					url: config_path__oa_person__add,
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
							$("#oa_person__submit").attr("value", top.getIl8n('submit') );
							top.myglobal.personid = response.id;
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#oa_person__submit").attr("value", top.getIl8n('submit') );
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
			id: 'oa_person__modify',
			fields: [
				 { display: top.getIl8n('name'), name: "name", validate: { required:true }, type: "text" }	
				 
				,{ display: top.getIl8n('oa_person','birthday'), name: "birthday", type: "date" }	
				,{ display: top.getIl8n('oa_person','card'), name: "card", type: "select", options :{data : oa_person.config.card, valueField : "code" , textField: "value", slide: false } }	
				,{ display: top.getIl8n('oa_person','cardid'), name: "cardid", type: "text"}		
	
				,{ display: top.getIl8n('oa_person','height'), name: "height", type: "number" }	
				
				,{ display: top.getIl8n('oa_person','cellphone'), name: "cellphone", type: "number" }		
				,{ display: top.getIl8n('oa_person','email'), name: "email", type: "text" }	
				,{ display: top.getIl8n('oa_person','qq'), name: "qq", type: "number", newline: false }		

				,{ display: top.getIl8n('oa_person','nationality'), name: "nationality", type: "text" }					
				
				,{ display: top.getIl8n('oa_person','nation'), name: "nation", type: "select", options :{data : oa_person.config.nation, valueField : "code" , textField: "value", slide: false }, newline: false  }
				,{ display: top.getIl8n('oa_person','gender'), name: "gender", type: "select", options :{data : oa_person.config.gender, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('oa_person','marriage'), name: "marriage", type: "select", options :{data : oa_person.config.marriage, valueField : "code" , textField: "value", slide: false }, newline: false  }
				,{ display: top.getIl8n('oa_person','degree'), name: "degree", type: "select", options :{data : oa_person.config.degree, valueField : "code" , textField: "value", slide: false } }
				,{ display: top.getIl8n('oa_person','politically'), name: "politically", type: "select", options :{data : oa_person.config.politically, valueField : "code" , textField: "value", slide: false }, newline: false  }
				
				,{ display: top.getIl8n('oa_person','photo'), name: "photo", type: "text" }	
				
				,{ display: top.getIl8n('oa_person','degree_school'), name: "degree_school", type: "text" }	
				,{ display: top.getIl8n('oa_person','degree_school_code'), name: "degree_school_code", type: "text" , newline: false}		
				,{ display: top.getIl8n('oa_person','address_birth'), name: "address_birth", type: "text" }	
				,{ display: top.getIl8n('oa_person','address_birth_code'), name: "address_birth_code", type: "text" , newline: false }		
				,{ display: top.getIl8n('oa_person','address'), name: "address", type: "text" }	
				,{ display: top.getIl8n('oa_person','address_code'), name: "address_code", type: "text" , newline: false }
				
			]
		};
		
		$(document.body).append("<form id='form'></form>");
		$('#form').ligerForm(config);	
		
		$("#photo").attr("disabled",true);
		$("#photo").css("background-color","#EEEEEE");
		$("#photo").parent().css("background-color","#EEEEEE");				
		$("#photo").parent().parent().next().css("width","80px").append("&nbsp;<a href='#' onclick='oa_person.uploadPhoto()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$('#form').append("<img class='person_photo' id='person_photo__img' src='../file/defaultphoto.jpg' />");
		
		$("#address_birth_code").attr("disabled",true);
		$("#degree_school_code").attr("disabled",true);
		$("#address_code").attr("disabled",true);
		$("#address_birth_code").css("background-color","#EEEEEE");
		$("#address_birth_code").parent().css("background-color","#EEEEEE");	
		$("#degree_school_code").css("background-color","#EEEEEE");
		$("#degree_school_code").parent().css("background-color","#EEEEEE");	
		$("#address_code").css("background-color","#EEEEEE");
		$("#address_code").parent().css("background-color","#EEEEEE");	
		$("#degree_school").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_person.getSchool()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$("#address_birth").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_person.getAddressMode=2;oa_person.getAddress()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
		$("#address").parent().parent().next().append("&nbsp;<a href='#' onclick='oa_person.getAddressMode=1;oa_person.getAddress()' ><div class='form_dilog_tip' >&nbsp;</div></a>");
						
		$('#form').append('<br/><br/><br/><br/><input type="submit" value="'+top.getIl8n('modify')+'" id="oa_person__submit" class="l-button l-button-submit" />' );
		
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
				$("#oa_person__submit").attr("value",top.getIl8n('waitting'));
				
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
				
				data.id = getParameter("id", window.location.toString() );
				
				$.ajax({
					url: config_path__oa_person__modify,
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
							$("#oa_person__submit").attr("value", top.getIl8n('submit') );
							top.myglobal.personid = response.id;
						//服务端添加失败
						}else{
							alert(response.msg);
							basic_user.ajaxState = false;
							$("#oa_person__submit").attr("value", top.getIl8n('submit') );
						}
					},
					error : function(){
						alert(top.il8n.disConnect);
					}
				});	
			}
		});
		
		$.ajax({
			url: config_path__oa_person__view
			,data: {
				id: getParameter("id", window.location.toString() )
				
				//服务端权限验证所需
				,executor: top.basic_user.loginData.username
				,session: top.basic_user.loginData.session
			}
			,type: "POST"
			,dataType: 'json'						
			,success: function(response) {	
				if(response.status!="1"){
					alert(response.msg);
					return;
				}
			    var data = response.data;
			    for (x in data) {
			    	if(x.substring(x.length-1,x.length)=="_"){
			    		eval("delete data."+x+";");
			    	}
			    }
			    
			    for (x in data) {
			    	if(x=="id")continue;

			    	eval("var thevalue = data."+x+";");
			    	if(x=="types")thevalue = thevalue.replace(",",";");
			    	if(x=="expert_gruops")thevalue = thevalue.replace(",",";");

			    	$.ligerui.get(x).setValue(thevalue);			
			    	
			    }
			    
			    $('#person_photo__img').attr("src",data.photo);			
			}
		});
	}	
	
	,view: function(){
		var id = getParameter("id", window.location.toString() );
    	$(document.body).html("<div id='menu'  ></div><div id='content' style='width:"+($(window).width()-170)+"px;margin-top:5px;'></div>");
    	var htmls = "";
    	$.ajax({
            url: config_path__oa_person__view
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

            	if(typeof(data.photo)=="undefined") data.photo = '../file/tavatar.gif';
            	for(var j in data){   
            		if(j=='sql'||j=='card'||j=='gender'||j=='nation'||j=='ismarried'||j=='degree'||j=='politically')continue;
            		if(j=='photo'){
        				htmls += '<div style="position:absolute;right:5px;top:5px;background-color: rgb(220,250,245);width:166px;height:176px;"><img style="margin:2px;" src="'+data[j]+'" width="160" height="170" /></div>'
        				continue;
            		}
            		if(j=='id'||j=='remark')htmls+="<div style='width:100%;float:left;display:block;margin-top:5px;'/>";
            		            		
            		if(j=='remark'||j=='types_'||j=='degree_school'||j=='address_birth'||j=='address'||j=='cardid'){
	            		eval("var key = getIl8n('oa_person','"+j+"');");
	            		htmls += "<span class='view_lable' style='width:95%'>"+key+"</span><span style='width:95%' class='view_data'>"+data[j]+"</span>";
            		}else{
            			eval("var key = getIl8n('oa_person','"+j+"');");
                		htmls += "<span class='view_lable'>"+key+"</span><span class='view_data'>"+data[j]+"</span>";
            		}
            	}; 
            	
            	$("#content").html(htmls);
            }
		});
	}
};