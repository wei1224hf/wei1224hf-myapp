var  config_path = ""
	
	//没有服务端
	/*
	,config_path__basic_user__loadConfig = "../data/basic_user__loadConfig.txt"
	,config_path__basic_user__login = "../data/basic_user__login.txt"
	,config_path__basic_user__logout = "../data/basic_user__logout.txt"
	,config_path__basic_user__updateSession = "../data/basic_user__updateSession.txt"		
	,config_path__basic_user__grid = "../data/basic_user__grid.txt"
	,config_path__basic_user__add = "../data/basic_user__add.txt"
	,config_path__basic_user__add_register = "../data/basic_user__add_register.txt"
	,config_path__basic_user__remove = "../data/basic_user__remove.txt"
	,config_path__basic_user__modify = "../data/basic_user__modify.txt"
	,config_path__basic_user__modify_myself = "../data/basic_user__modify_myself.txt"		
	,config_path__basic_user__view = "../data/basic_user__view.txt"
	,config_path__basic_user__group_get = "../data/basic_user__group_get.txt"
	,config_path__basic_user__group_set = "../data/basic_user__group_set.txt"				
		
	,config_path__basic_group__add = "../data/basic_group__add.txt"
	,config_path__basic_group__modify = "../data/basic_group__modify.txt"
	,config_path__basic_group__view = "../data/basic_group__view.txt"
	,config_path__basic_group__remove = "../data/basic_group__remove.txt"
	,config_path__basic_group__permission_get = "../data/basic_group__permission_get.txt"
	,config_path__basic_group__permission_set = "../data/basic_group__permission_set.txt"
	,config_path__basic_group__grid = "../data/basic_group__grid.txt"
	,config_path__basic_group__loadConfig = "../data/basic_group__loadConfig.txt"
	*/
	
	//JAVA 服务端
    
	,config_path__basic_parameter__grid = "../jsp/myapp.jsp?class=basic_parameter&function=grid"
	,config_path__basic_parameter__add = "../jsp/myapp.jsp?class=basic_parameter&function=add"
	,config_path__basic_parameter__remove = "../jsp/myapp.jsp?class=basic_parameter&function=remove"
	,config_path__basic_parameter__lowerCodes = "../jsp/myapp.jsp?class=basic_parameter&function=lowerCodes"		
	,config_path__basic_parameter__resetMemory = "../jsp/myapp.jsp?class=basic_parameter&function=resetMemory"		
	
	,config_path__basic_user__loadConfig =		"../jsp/myapp.jsp?class=basic_user&function=loadConfig"								
	,config_path__basic_user__login =			"../jsp/myapp.jsp?class=basic_user&function=login"	
	,config_path__basic_user__logout =			"../jsp/myapp.jsp?class=basic_user&function=logout"	
	,config_path__basic_user__updateSession =	"../jsp/myapp.jsp?class=basic_user&function=updateSession"	
	,config_path__basic_user__grid =			"../jsp/myapp.jsp?class=basic_user&function=grid"	
	,config_path__basic_user__add =				"../jsp/myapp.jsp?class=basic_user&function=add"	
	,config_path__basic_user__add_register =	"../jsp/myapp.jsp?class=basic_user&function=add_register"	
	,config_path__basic_user__remove =			"../jsp/myapp.jsp?class=basic_user&function=remove"	
	,config_path__basic_user__modify =			"../jsp/myapp.jsp?class=basic_user&function=modify"	
	,config_path__basic_user__modify_myself =	"../jsp/myapp.jsp?class=basic_user&function=modify_myself"	
	,config_path__basic_user__view =			"../jsp/myapp.jsp?class=basic_user&function=view"	
	,config_path__basic_user__group_get =		"../jsp/myapp.jsp?class=basic_user&function=group_get"	
	,config_path__basic_user__group_set =		"../jsp/myapp.jsp?class=basic_user&function=group_set"		
		
	,config_path__basic_group__add =			"../jsp/myapp.jsp?class=basic_group&function=add"		
	,config_path__basic_group__modify =			"../jsp/myapp.jsp?class=basic_group&function=modify"		
	,config_path__basic_group__view =			"../jsp/myapp.jsp?class=basic_group&function=view"		
	,config_path__basic_group__remove =			"../jsp/myapp.jsp?class=basic_group&function=remove"		
	,config_path__basic_group__permission_get =	"../jsp/myapp.jsp?class=basic_group&function=permission_get"		
	,config_path__basic_group__permission_set =	"../jsp/myapp.jsp?class=basic_group&function=permission_set"		
	,config_path__basic_group__grid =			"../jsp/myapp.jsp?class=basic_group&function=grid"		
	,config_path__basic_group__loadConfig =		"../jsp/myapp.jsp?class=basic_group&function=loadConfig"
		
	,config_path__oa_person__loadConfig = 		"../jsp/myapp.jsp?class=oa_person&function=loadConfig"
	,config_path__oa_person__add =				"../jsp/myapp.jsp?class=oa_person&function=add"	
	,config_path__oa_person__view =				"../jsp/myapp.jsp?class=oa_person&function=view"			
	,config_path__oa_person__modify =			"../jsp/myapp.jsp?class=oa_person&function=modify"					
	,config_path__oa_person__uploadPhoto = 		"../jsp/doUpload.jsp?class=oa_person&function=uploadPhoto"		
	,config_path__oa_person__lowerCodes =   	"../jsp/myapp.jsp?class=oa_person&function=lowerCodes"		
		
	,config_path__oa_plan__loadConfig = 		"../jsp/myapp.jsp?class=oa_plan&function=loadConfig"
	,config_path__oa_plan__gantt =		 		"../jsp/myapp.jsp?class=oa_plan&function=gantt"
	,config_path__oa_plan__grid =		 		"../jsp/myapp.jsp?class=oa_plan&function=grid"		
	,config_path__oa_plan__add =				"../jsp/myapp.jsp?class=oa_plan&function=add"	
	,config_path__oa_plan__view =				"../jsp/myapp.jsp?class=oa_plan&function=view"			
	,config_path__oa_plan__modify =				"../jsp/myapp.jsp?class=oa_plan&function=modify"	
	,config_path__oa_plan__remove =				"../jsp/myapp.jsp?class=oa_plan&function=remove"		
	,config_path__oa_plan__lowerCodes =   		"../jsp/myapp.jsp?class=oa_plan&function=lowerCodes"		
	,config_path__oa_plan__checkCode =   		"../jsp/myapp.jsp?class=oa_plan&function=checkCode"		
	,config_path__oa_plan__usergrid =   		"../jsp/myapp.jsp?class=oa_plan&function=usergrid"		
	,config_path__oa_plan__groupgrid =   		"../jsp/myapp.jsp?class=oa_plan&function=groupgrid"	
	,config_path__oa_plan__quotesgrid = 		"../jsp/myapp.jsp?class=oa_plan&function=quotesgrid"	
	,config_path__oa_plan__setWeight =   		"../jsp/myapp.jsp?class=oa_plan&function=setWeight"		
	,config_path__oa_plan__upload = 			"../jsp/doUpload.jsp?class=oa_plan&function=upload"		
	,config_path__oa_plan__download = 			"../jsp/myapp.jsp?class=oa_plan&function=download"
	,config_path__oa_plan__examine =			"../jsp/myapp.jsp?class=oa_plan&function=examine"		
	,config_path__oa_plan__statistics_time = 	"../jsp/myapp.jsp?class=oa_plan&function=statistics_time"	
	,config_path__oa_plan__statistics_attribute = 	"../jsp/myapp.jsp?class=oa_plan&function=statistics_attribute"	
		
	,config_path__oa_work__add =			"../jsp/myapp.jsp?class=oa_work&function=add"		
	,config_path__oa_work__modify =			"../jsp/myapp.jsp?class=oa_work&function=modify"		
	,config_path__oa_work__view =			"../jsp/myapp.jsp?class=oa_work&function=view"		
	,config_path__oa_work__remove =			"../jsp/myapp.jsp?class=oa_work&function=remove"		
	,config_path__oa_work__grid =			"../jsp/myapp.jsp?class=oa_work&function=grid"		
	,config_path__oa_work__loadConfig =		"../jsp/myapp.jsp?class=oa_work&function=loadConfig"
	,config_path__oa_work__lowerCodes =   	"../jsp/myapp.jsp?class=oa_work&function=lowerCodes"		
	,config_path__oa_work__bound = 			"../jsp/myapp.jsp?class=oa_work&function=bound"		
	,config_path__oa_work__upload = 		"../jsp/doUpload.jsp?class=oa_work&function=upload"		
	,config_path__oa_work__download = 		"../jsp/myapp.jsp?class=oa_work&function=download"			
	,config_path__oa_work__examine = 		"../jsp/myapp.jsp?class=oa_work&function=examine"	
	,config_path__oa_work__statistics_time = 		"../jsp/myapp.jsp?class=oa_work&function=statistics_time"	
	,config_path__oa_work__statistics_attribute = 	"../jsp/myapp.jsp?class=oa_work&function=statistics_attribute"		

	,config_path__government_building__add =			"../jsp/myapp.jsp?class=government_building&function=add"		
	,config_path__government_building__modify =			"../jsp/myapp.jsp?class=government_building&function=modify"		
	,config_path__government_building__view =			"../jsp/myapp.jsp?class=government_building&function=view"		
	,config_path__government_building__remove =			"../jsp/myapp.jsp?class=government_building&function=remove"		
	,config_path__government_building__grid =			"../jsp/myapp.jsp?class=government_building&function=grid"		
	,config_path__government_building__loadConfig =		"../jsp/myapp.jsp?class=government_building&function=loadConfig"
	,config_path__government_building__lowerCodes =   	"../jsp/myapp.jsp?class=government_building&function=lowerCodes"		
	,config_path__government_building__bound = 			"../jsp/myapp.jsp?class=government_building&function=bound"		
	,config_path__government_building__uploadPhoto = 	"../jsp/doUpload.jsp?class=government_building&function=uploadPhoto"
	,config_path__government_building__upload = 		"../jsp/doUpload.jsp?class=government_building&function=upload"		
	,config_path__government_building__download = 		"../jsp/myapp.jsp?class=government_building&function=download"	
	,config_path__government_building__statistics_time = 		"../jsp/myapp.jsp?class=government_building&function=statistics_time"	
	,config_path__government_building__statistics_attribute = 	"../jsp/myapp.jsp?class=government_building&function=statistics_attribute"		
	,config_path__government_building__bind = 			"../jsp/myapp.jsp?class=government_building&function=bind"		
		
	,config_path__government_family__add =			"../jsp/myapp.jsp?class=government_family&function=add"		
	,config_path__government_family__modify =		"../jsp/myapp.jsp?class=government_family&function=modify"		
	,config_path__government_family__view =			"../jsp/myapp.jsp?class=government_family&function=view"		
	,config_path__government_family__remove =		"../jsp/myapp.jsp?class=government_family&function=remove"		
	,config_path__government_family__grid =			"../jsp/myapp.jsp?class=government_family&function=grid"		
	,config_path__government_family__loadConfig =	"../jsp/myapp.jsp?class=government_family&function=loadConfig"
	,config_path__government_family__lowerCodes =   "../jsp/myapp.jsp?class=government_family&function=lowerCodes"		
	,config_path__government_family__bound = 		"../jsp/myapp.jsp?class=government_family&function=bound"		
	,config_path__government_family__uploadPhoto = 	"../jsp/doUpload.jsp?class=government_family&function=uploadPhoto"
	,config_path__government_family__upload = 		"../jsp/doUpload.jsp?class=government_family&function=upload"		
	,config_path__government_family__download = 	"../jsp/myapp.jsp?class=government_family&function=download"		
		
	,config_path__government_resident__add =			"../jsp/myapp.jsp?class=government_resident&function=add"		
	,config_path__government_resident__modify =		"../jsp/myapp.jsp?class=government_resident&function=modify"		
	,config_path__government_resident__view =			"../jsp/myapp.jsp?class=government_resident&function=view"		
	,config_path__government_resident__remove =		"../jsp/myapp.jsp?class=government_resident&function=remove"		
	,config_path__government_resident__grid =			"../jsp/myapp.jsp?class=government_resident&function=grid"		
	,config_path__government_resident__loadConfig =	"../jsp/myapp.jsp?class=government_resident&function=loadConfig"
	,config_path__government_resident__lowerCodes =   "../jsp/myapp.jsp?class=government_resident&function=lowerCodes"		
	,config_path__government_resident__bound = 		"../jsp/myapp.jsp?class=government_resident&function=bound"		
	,config_path__government_resident__uploadPhoto = 	"../jsp/doUpload.jsp?class=government_resident&function=uploadPhoto"
	,config_path__government_resident__upload = 		"../jsp/doUpload.jsp?class=government_resident&function=upload"		
	,config_path__government_resident__download = 	"../jsp/myapp.jsp?class=government_resident&function=download"	
		
	,config_path__government_company__add =			"../jsp/myapp.jsp?class=government_company&function=add"		
	,config_path__government_company__modify =		"../jsp/myapp.jsp?class=government_company&function=modify"		
	,config_path__government_company__view =		"../jsp/myapp.jsp?class=government_company&function=view"
	,config_path__government_company__remove =		"../jsp/myapp.jsp?class=government_company&function=remove"		
	,config_path__government_company__grid =			"../jsp/myapp.jsp?class=government_company&function=grid"		
	,config_path__government_company__loadConfig =	"../jsp/myapp.jsp?class=government_company&function=loadConfig"
	,config_path__government_company__lowerCodes =   "../jsp/myapp.jsp?class=government_company&function=lowerCodes"		
	,config_path__government_company__bound = 		"../jsp/myapp.jsp?class=government_company&function=bound"		
	,config_path__government_company__uploadPhoto = 	"../jsp/doUpload.jsp?class=government_company&function=uploadPhoto"
	,config_path__government_company__upload = 		"../jsp/doUpload.jsp?class=government_company&function=upload"		
	,config_path__government_company__download = 	"../jsp/myapp.jsp?class=government_company&function=download"			
		
	,config_path__gis_polygon__add =				"../jsp/myapp.jsp?class=gis_polygon&function=add"	
	,config_path__gis_polygon__read =				"../jsp/myapp.jsp?class=gis_polygon&function=read"	
	,config_path__gis_polygon__remove =				"../jsp/myapp.jsp?class=gis_polygon&function=remove"
		
	,config_path__service_resource__qq_schools =   	"../jsp/myapp.jsp?class=service_resource&function=qq_schools"	
    

	//PHP 服务端
	/*
	,config_path__basic_user__loadConfig =		"../php/myapp.php?class=basic_user&function=loadConfig"								
	,config_path__basic_user__login =			"../php/myapp.php?class=basic_user&function=login"	
	,config_path__basic_user__logout =			"../php/myapp.php?class=basic_user&function=logout"	
	,config_path__basic_user__updateSession =	"../php/myapp.php?class=basic_user&function=updateSession"	
	,config_path__basic_user__grid =			"../php/myapp.php?class=basic_user&function=grid"	
	,config_path__basic_user__add =				"../php/myapp.php?class=basic_user&function=add"	
	,config_path__basic_user__add_register =	"../php/myapp.php?class=basic_user&function=add_register"	
	,config_path__basic_user__remove =			"../php/myapp.php?class=basic_user&function=remove"	
	,config_path__basic_user__modify =			"../php/myapp.php?class=basic_user&function=modify"	
	,config_path__basic_user__modify_myself =	"../php/myapp.php?class=basic_user&function=modify_myself"	
	,config_path__basic_user__view =			"../php/myapp.php?class=basic_user&function=view"	
	,config_path__basic_user__group_get =		"../php/myapp.php?class=basic_user&function=group_get"	
	,config_path__basic_user__group_set =		"../php/myapp.php?class=basic_user&function=group_set"		
		
	,config_path__basic_group__add =			"../php/myapp.php?class=basic_group&function=add"		
	,config_path__basic_group__modify =			"../php/myapp.php?class=basic_group&function=modify"		
	,config_path__basic_group__view =			"../php/myapp.php?class=basic_group&function=view"		
	,config_path__basic_group__remove =			"../php/myapp.php?class=basic_group&function=remove"		
	,config_path__basic_group__permission_get =	"../php/myapp.php?class=basic_group&function=permission_get"		
	,config_path__basic_group__permission_set =	"../php/myapp.php?class=basic_group&function=permission_set"		
	,config_path__basic_group__grid =			"../php/myapp.php?class=basic_group&function=grid"		
	,config_path__basic_group__loadConfig =		"../php/myapp.php?class=basic_group&function=loadConfig"
	*/

    //C# 服务端
	/*
	, config_path__basic_parameter__grid = "../aspx/myapp.aspx?class=basic_parameter&function=grid"
	, config_path__basic_parameter__add = "../aspx/myapp.aspx?class=basic_parameter&function=add"
	, config_path__basic_parameter__remove = "../aspx/myapp.aspx?class=basic_parameter&function=remove"
	, config_path__basic_parameter__lowerCodes = "../aspx/myapp.aspx?class=basic_parameter&function=lowerCodes"
	, config_path__basic_parameter__resetMemory = "../aspx/myapp.aspx?class=basic_parameter&function=resetMemory"

	, config_path__basic_user__loadConfig = "../aspx/myapp.aspx?class=basic_user&function=loadConfig"
	, config_path__basic_user__login = "../aspx/myapp.aspx?class=basic_user&function=login"
	, config_path__basic_user__logout = "../aspx/myapp.aspx?class=basic_user&function=logout"
	, config_path__basic_user__updateSession = "../aspx/myapp.aspx?class=basic_user&function=updateSession"
	, config_path__basic_user__grid = "../aspx/myapp.aspx?class=basic_user&function=grid"
	, config_path__basic_user__add = "../aspx/myapp.aspx?class=basic_user&function=add"
	, config_path__basic_user__add_register = "../aspx/myapp.aspx?class=basic_user&function=add_register"
	, config_path__basic_user__remove = "../aspx/myapp.aspx?class=basic_user&function=remove"
	, config_path__basic_user__modify = "../aspx/myapp.aspx?class=basic_user&function=modify"
	, config_path__basic_user__modify_myself = "../aspx/myapp.aspx?class=basic_user&function=modify_myself"
	, config_path__basic_user__view = "../aspx/myapp.aspx?class=basic_user&function=view"
	, config_path__basic_user__group_get = "../aspx/myapp.aspx?class=basic_user&function=group_get"
	, config_path__basic_user__group_set = "../aspx/myapp.aspx?class=basic_user&function=group_set"

	, config_path__basic_group__add = "../aspx/myapp.aspx?class=basic_group&function=add"
	, config_path__basic_group__modify = "../aspx/myapp.aspx?class=basic_group&function=modify"
	, config_path__basic_group__view = "../aspx/myapp.aspx?class=basic_group&function=view"
	, config_path__basic_group__remove = "../aspx/myapp.aspx?class=basic_group&function=remove"
	, config_path__basic_group__permission_get = "../aspx/myapp.aspx?class=basic_group&function=permission_get"
	, config_path__basic_group__permission_set = "../aspx/myapp.aspx?class=basic_group&function=permission_set"
	, config_path__basic_group__grid = "../aspx/myapp.aspx?class=basic_group&function=grid"
	, config_path__basic_group__loadConfig = "../aspx/myapp.aspx?class=basic_group&function=loadConfig"
	*/
	;