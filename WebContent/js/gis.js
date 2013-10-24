/*
var lon = 5;
var lat = 40;
var zoom = 5;
var map = null,
	drawFeature = null,
	polygonLayer = null,vectorLayer = null;

var highlightCtrl = null
,selectCtrl = null;
 
var init = function(){
    map = new OpenLayers.Map( 'map', {maxResolution:1.40625/2} );	
    
	var baseLayer = new OpenLayers.Layer.TMS("baseLayer"," ",{
		layername:"madcm_20130320",type:"png",
		getURL: function (bounds) {
			bounds = this.adjustBounds(bounds);
			var res = this.map.getResolution();
			var x = Math.round((bounds.left - this.tileOrigin.lon) / (res * this.tileSize.w));
			var y = Math.round((bounds.top + this.tileOrigin.lat) / (res * this.tileSize.h));
			var z = this.serverResolutions != null ?
			OpenLayers.Util.indexOf(this.serverResolutions, res) :
			this.map.getZoom() + this.zoomOffset;
			var shift = z / 2;
			var half = 2 << shift;
			 
			var digits = 1;
			if (half > 10) {
				digits = Math.ceil(  Math.floor(Math.log(half)/Math.log(10)) + 1 );
			}
			var halfx = Math.floor( x / half );
			var halfy = Math.floor( y / half );

			//澶ц繛甯侲閮藉競鍦板浘
			//var path = "http://hbpic0.go2map.com/seamless1/dalian/mappic/png"+(7-z)+"/"+x+","+y*(-1)+".jpg";
			//var path = "../jsp/myapp.jsp?class=service_resource&function=eds_map&path="+(7-z)+"/"+x+","+y*(-1)+".jpg";
			var path = "../file/gis/"+(7-z)+"/"+x+","+y*(-1)+".jpg";
			
			return  path;
		}},
		{ tileSize: new OpenLayers.Size(256,256) }
	);
					
    map.addLayer(baseLayer);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
	map.addControl(new OpenLayers.Control.MousePosition());
    map.setCenter(new OpenLayers.LonLat(-132, -52), zoom);
	
    var enableRead = getParameter("readserver", window.location.toString() );
    if(enableRead!=""){
    	
    	enableReadServer();
    	readVectorLayer();
    }
    
    var enableAction = getParameter("enableAction", window.location.toString() );
    if(enableAction!=""){
    	gisActions.enableAction();
    }    
};



var afterDraw = function(data){alert(data);};
var enableDraw = function(){
    if(drawFeature==null){ 
	    polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");	
	    map.addLayers([ polygonLayer]);
	    drawFeature = new OpenLayers.Control.DrawFeature(polygonLayer,OpenLayers.Handler.Polygon,{
        callbacks : {
            "done": function(x,y,z){
        		var gisStr = x.toString();
        		
        		$.ajax({
        			url: config_path__gis_polygon__add
        			,data: {
        				data: $.ligerui.toJSON({
        					 ogc_geom: "GeomFromText('"+gisStr+"')"
        					,type: '2'
        					,name: 'building'
        					,height: '0.01'
        				})
        				
        				,executor: top.basic_user.loginData.username
        				,session: top.basic_user.loginData.session
        			}
        			,type: "POST"
        			,dataType: 'json'						
        			,success: function(response) {	
        			    var data = response.id;
        			    afterDraw(data);
        			}
        		});
        	},
            "point": function(x,y,z){
        		//TODO
             }
        }});
		map.addControl(drawFeature);
    }
	drawFeature.activate();            
};

var onfeatureSelected = function(feature){};
var theSelectedItem = null;
var draw = function(array_WKT){
	if(drawFeature!=null)drawFeature.deactivate();
	
	if(vectorLayer == null){
		vectorLayer = new OpenLayers.Layer.Vector("vectorLayer",{
			styleMap: new OpenLayers.StyleMap({
				 'default': {fillOpacity: 0.8, strokeOpacity: 0.01 , fillColor: "${color}" }
				,'temporary': {strokeColor: "black",strokeWidth:0.5,strokeOpacity: 0.8, fillColor: "#000000", fillOpacity: 0.9 }
			})			
		});
		map.addLayer(vectorLayer);
		
		highlightCtrl = new OpenLayers.Control.SelectFeature(vectorLayer, {
			hover: true,
			highlightOnly: true,
			renderIntent: "temporary",
			eventListeners: {				
				featurehighlighted: function(f){
					var x = f.feature.geometry.getBounds().getCenterLonLat();
					var pos = map.getPixelFromLonLat(x);					
					
					$('#polygon_title').css("top",pos.y+"px").css("left",pos.x+"px").html(f.feature.attributes.name);
					$('#polygon_title').show();
					
				}	
				,featureunhighlighted: function(f){
					$('#polygon_title').hide();	
				}
			}

		});
		map.addControl(highlightCtrl);
		highlightCtrl.activate();   
		
		
		selectCtrl = new OpenLayers.Control.SelectFeature(vectorLayer,
			{clickout: true,
			onSelect:function(feature){
				theSelectedItem = feature;
				$('#polygon_title').hide();						
				
				onfeatureSelected(feature);
			}}			
		);
		map.addControl(selectCtrl);
		selectCtrl.activate();
		
	}else{
		vectorLayer.removeAllFeatures();
	}

	var list = [];
	for(var i1=0;i1<array_WKT.length;i1++){
		if(array_WKT[i1].WKT == null)continue;
		var polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.fromWKT(array_WKT[i1].WKT),
			{
				 id: array_WKT[i1].id
				,type: array_WKT[i1].type
				,name: array_WKT[i1].name
				,color: array_WKT[i1].color
			}				
		);

		list.push(polygonFeature);			
	}

	vectorLayer.addFeatures(list);
};
var mapBounds = null;
var vectorLayerReadingAjax = 0;
var readVectorLayer = function(){

	//濡傛灉鏈嶅姟绔殑鏁版嵁杩樻病鏈夎繑鍥�灏变笉璇诲彇,浠ラ伩鍏嶅悜鏈嶅姟绔噸澶嶈鍙�
	if(vectorLayerReadingAjax)return;
	vectorLayerReadingAjax = true;
	

	//寰楀埌宸︿笂瑙掕窡鍙充笅瑙掔殑鍧愭爣
	var bounds = map.calculateBounds();
	var bottom = bounds.bottom,
		left = bounds.left,
		right = bounds.right,
		top = bounds.top;

	//鏈嶅姟绔殑璺緞,鍦�config.js 涓�
	$.ajax({
		url: config_path__gis_polygon__read
		,type: 'POST'
		,data: {
			data: $.ligerui.toJSON({
			 right: right
			,top: top
			,left: left
			,bottom: bottom			
			
			,zoom: map.zoom
			}),executor:"admin"
			}
		,dataType: 'json'
		,success : function(response) {
			mapBounds = bounds;
			//鏈嶅姟绔暟鎹繑鍥炲悗,鏀瑰彉 閫氫俊鐘舵� ,娓呯┖褰撳墠鎵�湁鐭㈤噺灞�閲嶆柊缁樺埗涓�亶
			vectorLayerReadingAjax = false;
			if(vectorLayer!=null){
				vectorLayer.removeAllFeatures();
			}				
						
			draw(response.Rows);
		}
	});
};


var enableReadServer = function(){
	
	map.events.register("moveend",map,function(){
		var mapBounds2 = map.calculateBounds();

		//濡傛灉鐢ㄦ埛鎷栨洺鐨勮窛绂诲皬浜庡綋鍓嶅睆骞�1/2 鐨勯暱鎴栧,灏变笉璇诲彇
		if( (Math.abs(mapBounds.right-mapBounds2.right) > Math.abs(mapBounds.right - mapBounds.left)/2)||
			(Math.abs(mapBounds.top-mapBounds2.top) > Math.abs(mapBounds.top - mapBounds.bottom)/2)){
			readVectorLayer();	
		}
		
	});

};

var disableReadServer =  function(){
	//TODO
	//map.clearOverlays();
	map.removeEventListener("dragend");
	map.removeEventListener("zoomend");
};

var gisActions = {
	
	enableAction: function(){

			var domstr = "<div id='toptoolbar' style='position: absolute;top: 0px;right: 0px;width: 400px;z-index: 999;'></div> ";
			$('#map').append(domstr);
            $("#toptoolbar").ligerToolBar({ items: [
                {
                    text: '澧炲姞', click: function (item)
                    {
                		enableDraw();
                    }},
                { line:true },
                { text: '缁戝畾', click: gisActions.bound },
                { line:true },
                { text: '鍒犻櫎', click: gisActions.remove }
            ]
            });

		
	}
	,remove: function(){
		
		$.ajax({
			url: config_path__gis_polygon__remove
			,type: 'POST'
			,data: {
				data: $.ligerui.toJSON({
					 id: theSelectedItem.attributes.id
				}),executor:"admin"
				}
			,dataType: 'json'
			,success : function(response) {
				alert('ok');
			}
		});
	}
	
	,bound: function(){
		
	}
	,panTo: function(x,y){
		map.panTo(new OpenLayers.LonLat(x, y));
	}
	
		
};
*/

var map;
var mygis = {
		
	type: "tianditu"
	,init: function(){
		OpenLayers.Util.onImageLoadError = function(){
		     this.src = "../file/blank.png";
		};
		map = new OpenLayers.Map({
		     div: "map"
		    ,projection: "EPSG:4326"
		    ,numZoomLevels: 20
		});    
		
		var wmts = new OpenLayers.Layer.WMTS({
		    name: "BackGround",
		    url: "http://t6.tianditu.cn/vec_c/wmts",
		    format: "tiles",
		    layer: "vec",
		    style: "default",
		    matrixSet: "c",
		    opacity: 0.7,		        
		    isBaseLayer: true
		});   
		
		var wmts3 = new OpenLayers.Layer.WMTS({
		    name: "Image",
		    url: "http://t6.tianditu.cn/img_c/wmts",
		    format: "tiles",
		    layer: "img",
		    style: "default",
		    matrixSet: "c",
		    opacity: 0.7,		        
		    isBaseLayer: true
		});   
		
		var wmts4 = new OpenLayers.Layer.WMTS({
		    name: "Road",
		    url: "http://t6.tianditu.cn/cia_c/wmts",
		    format: "tiles",
		    layer: "cia",
		    style: "default",
		    matrixSet: "c",
		    opacity: 0.7,		        
		    isBaseLayer: false,
		    visibility: false
		}); 	
		
		var wmts2 = new OpenLayers.Layer.WMTS({
		    name: "Markplace",
		    url: "http://t6.tianditu.cn/cva_c/wmts",
		    format: "tiles",
		    layer: "cva",
		    style: "default",
		    matrixSet: "c",
		    opacity: 0.7,		        
		    isBaseLayer: false
		});       		    
		
		map.addLayers([ wmts,wmts2,wmts3,wmts4]);
		
		map.addControl(new OpenLayers.Control.LayerSwitcher());
		map.addControl(new OpenLayers.Control.MousePosition());
		map.setCenter(new OpenLayers.LonLat(114.92985, 25.83433), 12);
	}

	,add: function(){
		mygis.init();
		map.setCenter(new OpenLayers.LonLat(114.92985, 25.83433), 18);
		var polygonLayer = new OpenLayers.Layer.Vector("Draw");	
	    map.addLayer(polygonLayer);
	    drawFeature = new OpenLayers.Control.DrawFeature(polygonLayer,OpenLayers.Handler.Polygon,{
        callbacks : {
            "done": function(x,y,z){
        		var gisStr = x.toString();
        		
        		$.ajax({
        			url: config_path__gis_polygon__add
        			,data: {
        				data: $.ligerui.toJSON({
        					 ogc_geom: "GeomFromText('"+gisStr+"')"
        					,type: '2'
        					,name: 'building'
        					,height: '0.01'
        				})
        				
        				,zoom: map.zoom
        				,executor: top.basic_user.loginData.username
        				,session: top.basic_user.loginData.session
        			}
        			,type: "POST"
        			,dataType: 'json'						
        			,success: function(response) {	
        			    var data = response.id;
        			}
        		});
        	},
            "point": function(x,y,z){
        		//TODO
             }
        }});
		map.addControl(drawFeature);
		drawFeature.activate();
		this.readBuildings();
		
		map.events.register("moveend",map,function(){
			var mapBounds2 = map.calculateBounds();
			//console.debug(mapBounds2);
			if( (Math.abs(mygis.mapBounds.right-mapBounds2.right) > Math.abs(mygis.mapBounds.right - mygis.mapBounds.left)/3)||
				(Math.abs(mygis.mapBounds.top-mapBounds2.top) > Math.abs(mygis.mapBounds.top - mygis.mapBounds.bottom)/3)){
				mygis.readBuildings();	
			}
		});
		map.events.register("zoomend",map,function(){
			mygis.readBuildings();	
		});
		
	}
	
	,onfeatureSelected: function(feature){}
	,mapForAll: function(){
		mygis.init();
		map.setCenter(new OpenLayers.LonLat(114.92985, 25.83433), 18);
		mygis.onfeatureSelected = function(f){
			if(f.attributes.code=="null"){
				alert("no bouding");return;
			}
			var zoom = map.zoom;
			if(zoom>=16){//楼宇层

				var id = f.attributes.code;
                if(top.$.ligerui.get("government_building__view_"+id)){
                    top.$.ligerui.get("government_building__view_"+id).show();
                    return;
                }					
				top.$.ligerDialog.open({ 
					url: 'government_building__view.html?id='+id+'&random='+Math.random()
					,height: 350
					,width: 590
					,isHidden: false
					, showMax: true
					, showToggle: true
					, showMin: true						
					,id: 'government_building__view_'+id
					, modal: false
				});	
				
		        top.$.ligerui.get("government_building__view_"+id).close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_building__view_"+id));
		        };
			}else{
				var id = 1;
				if(top.$.ligerui.get("government_zone__view_"+id)){
	                    top.$.ligerui.get("government_zone__view_"+id).show();
	                    return;
                }					
				top.$.ligerDialog.open({ 
					url: 'government_zone__view.html?id='+id+'&random='+Math.random()
					,height: 350
					,width: 590
					,isHidden: false
					, showMax: true
					, showToggle: true
					, showMin: true		
					, title: "行政区划名称,比如 XXX街道,XXX社区"
					,id: 'government_zone__view_'+id
					, modal: false
				});	
				
		        top.$.ligerui.get("government_zone__view_"+id).close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_zone__view_"+id));
		        };
			}
		};
		
		map.events.register("moveend",map,function(){
			var mapBounds2 = map.calculateBounds();
			//console.debug(mapBounds2);
			if( (Math.abs(mygis.mapBounds.right-mapBounds2.right) > Math.abs(mygis.mapBounds.right - mygis.mapBounds.left)/3)||
				(Math.abs(mygis.mapBounds.top-mapBounds2.top) > Math.abs(mygis.mapBounds.top - mygis.mapBounds.bottom)/3)){
				mygis.readBuildings();	
			}
		});
		map.events.register("zoomend",map,function(){
			mygis.readBuildings();	
		});			
		
		this.readBuildings();	
	}
	
	,panTo: function(x,y){
		//map.panTo(new OpenLayers.LonLat(x, y));
		map.setCenter(new OpenLayers.LonLat(x, y), map.zoom);
	}
	
	,bindItemId: null
	,bind: function(){
		mygis.init();
		map.setCenter(new OpenLayers.LonLat(114.92985, 25.83433), 18);
		mygis.onfeatureSelected = function(f){
			var id = f.attributes.id;
			mygis.bindItemId = id;
		};
		
		map.events.register("moveend",map,function(){
			var mapBounds2 = map.calculateBounds();
			//console.debug(mapBounds2);
			if( (Math.abs(mygis.mapBounds.right-mapBounds2.right) > Math.abs(mygis.mapBounds.right - mygis.mapBounds.left)/3)||
				(Math.abs(mygis.mapBounds.top-mapBounds2.top) > Math.abs(mygis.mapBounds.top - mygis.mapBounds.bottom)/3)){
				mygis.readBuildings();	
			}
		});
		map.events.register("zoomend",map,function(){
			mygis.readBuildings();	
		});			
		
		this.readBuildings();	
	}
	
	,mapForLocate: function(){
		mygis.init();
		map.setCenter(new OpenLayers.LonLat(114.92985, 25.83433), 18);
		mygis.onfeatureSelected = function(f){
			var zoom = map.zoom;
			if(zoom>=16){//楼宇层

				var id = f.attributes.id;
                if(top.$.ligerui.get("government_building__view_"+id)){
                    top.$.ligerui.get("government_building__view_"+id).show();
                    return;
                }					
				var win = top.$.ligerDialog.open({ 
					url: 'government_building__view.html?id='+id+'&random='+Math.random()
					,height: 350
					,width: 590
					,isHidden: false
					, showMax: true
					, showToggle: true
					, showMin: true						
					,id: 'government_building__view_'+id
					, modal: false
				});	
				
				win.close = function(){
		            var g = this;
		            top.$.ligerui.win.removeTask(this);
		            g.unmask();
		            g._removeDialog();
		            top.$.ligerui.remove(top.$.ligerui.get("government_building__view_"+id));
		        };
			}
		};
	}
	
	,isReading: false
	,readBuildings: function(){		
		if(mygis.isReading)return;
		mygis.isReading = true;
		var bounds = map.calculateBounds();
		var bottom = bounds.bottom,
			left = bounds.left,
			right = bounds.right,
			top_ = bounds.top;

		$.ajax({
			url: config_path__gis_polygon__read
			,type: 'POST'
			,data: {
				data: $.ligerui.toJSON({
					 right: right
					,top: top_
					,left: left
					,bottom: bottom					
				})
                ,executor: top.basic_user.loginData.username
                ,session: top.basic_user.loginData.session
				,zoom: map.zoom
				}
			,dataType: 'json'
			,success : function(response) {
				mygis.isReading = false;
				mygis.mapBounds = bounds;

				if(mygis.vectorLayer!=null){
					mygis.vectorLayer.removeAllFeatures();
				}				
							
				mygis.draw(response.Rows);
			}
		});
	}
	
	,mapBounds: null
	,vectorLayer: null
	,draw: function(array_WKT){
		if(this.vectorLayer == null){
			this.vectorLayer = new OpenLayers.Layer.Vector("vectorLayer"
					,{
				styleMap: new OpenLayers.StyleMap({
					 'default': {strokeColor: "red",fillOpacity: 0.5, strokeOpacity: 0.9 ,strokeWidth: 1 , fillColor: "red",pointRadius: 5  }
					,'temporary': {strokeColor: "black",strokeWidth: 8 ,strokeColor:"red",pointRadius: 10,strokeOpacity: 0.5, fillColor: "#000000", fillOpacity: 1 }
				})			
			}
			);
			
			map.addLayer(this.vectorLayer);
			
			var highlightCtrl = new OpenLayers.Control.SelectFeature(mygis.vectorLayer, {
				hover: true,
				highlightOnly: true,
				renderIntent: "temporary",
				eventListeners: {				
					featurehighlighted: function(f){
						var x = f.feature.geometry.getBounds().getCenterLonLat();
						var pos = map.getPixelFromLonLat(x);					
						
						$('#polygon_title').css("top",pos.y+"px").css("left",pos.x+"px").html(f.feature.attributes.name);
						$('#polygon_title').show();
						
					}	
					,featureunhighlighted: function(f){
						$('#polygon_title').hide();	
					}
				}

			});
			map.addControl(highlightCtrl);
			highlightCtrl.activate();  
			
			var selectCtrl = new OpenLayers.Control.SelectFeature(mygis.vectorLayer,
				{clickout: true,
				onSelect:function(feature){
					theSelectedItem = feature;
					$('#polygon_title').hide();						
					
					mygis.onfeatureSelected(feature);
				}}			
			);
			map.addControl(selectCtrl);
			selectCtrl.activate();
			
			
		}else{
			mygis.vectorLayer.removeAllFeatures();
		}

		
		var list = [];

		for(var i1=0;i1<array_WKT.length;i1++){
			if(array_WKT[i1].wkt == null)continue;
			if(array_WKT[i1].wkt instanceof Array)array_WKT[i1].wkt="GEOMETRYCOLLECTION("+array_WKT[i1].wkt.toString()+")";
			var geometry = new OpenLayers.Geometry.fromWKT(array_WKT[i1].wkt);			
			var polygonFeature = new OpenLayers.Feature.Vector(geometry,
				{
					 id: array_WKT[i1].id
					,type: array_WKT[i1].type
					,name: array_WKT[i1].name
					,color: array_WKT[i1].color
					,code: array_WKT[i1].code 
				}				
			);
			
			list.push(polygonFeature);			
		}

		this.vectorLayer.addFeatures(list);
	}

};