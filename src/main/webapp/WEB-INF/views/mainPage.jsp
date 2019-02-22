<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="https://use.fontawesome.com/releases/v5.6.3/css/all.css"
	integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/"
	crossorigin="anonymous">
<!-- <link rel="stylesheet" type="text/css" href="chrome-extension://chklaanhfefbnpoihckbnefhakgolnmc/jsonview-core.css"> -->
<link rel="stylesheet" href="./../css/contents.css?ver=9"
	type="text/css">
<link rel="stylesheet" href="./../jjson/css/jjsonviewer.css?">
<!-- BootStrap CDN -->
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="./../jjson/js/jjsonviewer.js"></script>
<!-- <script src="src/main/webapp/WEB-INF/views/fakeLoader/fakeLoader.min.js"></script>
<link rel="stylesheet" href="/src/main/webapp/WEB-INF/views/fakeLoader/fakeLoader.css">
 -->
<title>엘라스틱 서치</title>
</head>
<body>
	<div class="row" id=row>
		<div class="col-md-4 row">
			<div class="col-sm-9">
				<label for="serverList" class="control-label">인덱스</label> <input
					class="form-control" type="text" id="serverList"
					placeholder="선택하세요." list="serverSearch">
				<datalist id="serverSearch">
					<option value="선택해주세요." />
					<option value="dev" />
					<option value="bmt" />
				</datalist>
			</div>
			<!-- <div class="col-sm-9">
				<label for="serverList" class="control-label two">서버</label> 
				<select id="serverList" name="file_name" class="form-control">
					<option value="">선택하세요.</option>
					<option value="dev">dev</option>
					<option value="bmt">bmt</option>
				</select>
			</div> -->
			<!-- onchange 사용해보기  -->
			<!-- 포이치문에 인덱스 값을 생성하여 각 리스트마다 색인을 넣어주고 그 값으로 이벤트로 발생시킬 때 varStatus 사용하자!!! -->
			<!--  <div class="col-sm-9">
					<label for="indexList" class="control-label">인덱스</label>
						<select id="indexList" class="form-control" disabled="disabled">
							<option value="indexListOption">선택하세요.</option>
						</select>
					</div> -->
			<!-- <div class="col-sm-9">
					<label for="typeList" class="control-label two">타입</label>
						<select id="typeList" class="form-control" disabled="disabled">
							<option value="">선택하세요.</option>
						</select>
					</div> -->
			<div class="col-sm-9">
				<label for="indexList" class="control-label">인덱스</label> <input
					class="form-control" type="text" id="indexList"
					placeholder="선택하세요." list="indexsearch" disabled="disabled">
				<datalist id="indexsearch">
					<option value="reset" />

				</datalist>
			</div>
			<div class="col-sm-9">
				<label for="typeList" class="control-label two">타입</label> <input
					class="form-control" type="text" id="typeList" placeholder="타입 입력창"
					list="typeSearch" disabled="disabled">
				<datalist id="typeSearch">
				</datalist>
			</div>
			<div class="col-sm-10">
				<label for="idSearch" class="control-label">아이디</label> <input
					class="form-control" type="text" id="idSearch"
					placeholder="아이디 입력창" disabled="disabled">
				<button id="SearchStart" type="button" class="btn btn-primary">검색</button>
				<button id="clearBtn" type="button" class="btn btn-primary">클리어</button>
			</div>
			<div class="col-sm-5">
				<label for="typeAndOr" class="control-label four">검색조건</label> <select
					id=typeAndOr class="form-control" disabled="disabled">
					<option value="">선택하세요.</option>
					<option value="and">and</option>
					<option value="or">or</option>
				</select>
			</div>
			<div class="col-sm-5">
				<label for="sizeData" class="control-label" style="">사이즈</label> <input
					type="number" id="sizeData" class="form-control size" min="1"
					value="10">
			</div>
			<div class="col-sm-5">
				<label for="sortType" class="control-label four" style="">정렬조건</label>
				<select id="sortType" class="form-control">
					<option value="">선택하세요.</option>
					<option value="DESC">DESC</option>
					<option value="ASC">ASC</option>
				</select>
			</div>
			<div class="col-sm-5">
				<label for="sortData" class="control-label" style="">정렬값</label> <input
					id="sortData" type="text" class="form-control" disabled="disabled">
			</div>
			<!--  <label for="dataCheck">정렬 조건</label>
					<select id = dataCheck>
						<option value="">선택하세요.</option>
						<option value="ASC">오름차순</option>
						<option value="DESC">내림차순</option>
					</select> -->
			<div class="col-sm-12" id="btn-group">
				<div class="col-sm-3">
					<label for="idKeySearch" id="labelKey" class="control-label one">키</label>
					<input type="text" id="idKeySearch" class="form-control datakey">
				</div>
				<div class="col-sm-4">
					<label for="idValueSearch" id="labelValue"
						class="control-label one">값</label> <input type="text"
						id="idValueSearch" class="form-control datavalue">
					<button id="getText" type="button" class="btn btn-warning">추가</button>
				</div>
				<div class="col-sm-12" id="createSearch"></div>
				<div class="col-sm-4" id="search-Btn"></div>
			</div>
		</div>
		<div id='total'></div>
		<div id='json' class="col-md-8 json"></div>



	</div>


	<script type="text/javascript">  
    //예)
	//val는 변수의 이름, value값이없으면 이름을 들고다닌다.
	 var indexSearch = $('#indexSearch');
	 var typeSearch = $('#typeSearch');
	 var idSearch = $('#idSearch');
	 var idKeySearch = $('#idKeySearch');
	 var idValueSearch = $('#idValueSearch');
	 var sizeData = $('#sizeData');
	 let typeAndOr = "";
	 let dataCheck = "";
	 var totalData = new Map();
 	 var searchData = new Map();
	 let obj = {};
	 var typesData = "";
	 var aaa="a"
 $(document).ready(function(){

	//index selectbox 데이터를 변경할 때
	 $('#indexList').change(() => {	
	 	var aaa = "1"
	 	var getIndex = {}; //추가 
	 	var config = {};
	 	getIndex = $('#indexList').val(); // 인덱스
	 	config = $('#serverList').val();
	 	$('#typeList').val('');
	 	$('#indexList').val('');
	 	$('#typeSearch').empty();
	 	$('<datalist>').children().remove();
	 		if(getIndex.length !== 0){
	 		
	 			$.post(`${location.origin}/typeList`, {
	 				
					getIndex : getIndex, //서버에 보낼 변수명
					config : config
			}, (result) => {
				console.log(aaa)
				typesData = result;
				$('#typeList').attr('disabled',false);
				$('#indexList').val(getIndex);
				$('#typeList').empty();
				$('#typeList').html('');
				$('#typeSearch').html('');
				// 물어보자 분기처리 데이터 리스트 값이 틀렸을 경우
				
				console.log("resultData ::: " + typesData)
				
				var defualt = "<option>"+"선택하세요"+"</option>";
		 		$('#typeList').append(defualt);
				$('#typeList').html("").append(defualt);
					for(var i = 0; i < typesData.length; i++){
						var html ="<option>"+typesData[i]+"</option>";
						
					$("#typeSearch").append(html);  // 타입 리스트에 연결 html 변수기능을 넣어 준다.	
						}
				
			}, 'json');
	 			
	 		}
	 	
		 
	 	
	 });
	
	  $('#typeList').change(()=>{
		 	var selectType = $('#typeList').val();
			$('#idSearch').attr('disabled',false);
			
			if(selectType == ""){
				$('#typeList').focus();
			}else{
			console.log('data : '+typesData);
				if(typesData.includes(selectType)){
					var result1 = typesData;
				}else{
					alert('타입 입력값이 틀렸습니다. 다시 입력해 주세요.')
				}
			}
			console.log("typeData" + selectType);
			console.log("typeChangeAfter ::: " + typesData);
		
		}); 
	 		
	 		
	 		$('#idList').change(() => {
	 			
	 		var IdLIst = $('#idList').val();
	 		
	 		var getId = $('#idList option:selected').val();
	 		$('#idSearch').val(getId);
	 		
	 		});
	 		
	 		$('#serverList').change(()=>{
	 		
	 		var config = $('#serverList').val();
	 			$('#indexList').val('');
	 			$('#typeList').val('');
		 		$('#json').html('');
		 		$('#total').html('');
		 		$('#indexsearch').empty()
		 		
		 		if(config === "reset") {
		 		alert("다시 선택해 주시기 바랍니다.");
		 		$('#indexList').val('')
		 		$(sizeData).val('')
		 		$('#json').html('')
		 		$("input[id^='creKey']").remove()
		 		$("input[id^='creValue']").remove()
		 		$("label[for^='creKey']").remove()
	    		$("label[for^='creValue']").remove()
		 		return false;
		 	}
	 			
	 		$.get(`${location.origin}/checkServer`, {
	 				
				config :config
			 
			}, (result) => {
				$('#indexList').attr('disabled',false);
				$('#indexList').empty();
				var defualt = "<option>"+"선택하세요"+"</option>";
		 		$('#indexList').append(defualt);
				
		 		console.log("serverList result" + result)
		 		
		 		
		 		
		 		$('#indexList').change(()=>{
		 			var indexData = $('#indexList').val();
		 		
		 			if(indexData.length == 0 || indexData == ""){
						$('#indexList').val('');
						$('#indexList').focus();
						return false;
				 	}else if(!result.includes(indexData)){
		 					alert("인덱값이 틀립니다. 다시 입력바랍니다.")
		 					$('#indexList').val('');
		 					$('#indexList').focus();
		 					return;
		 					
		 				}
		 		});
		 		
		 		
		 		
				for(var i = 0; i < result.length; i++){
					
					var html ="<option>"+result[i]+"</option>";
					
					$('#indexsearch').append(html);
				}
					
	 			
	 		},"json");
	 		
	 		});
		      
	 		$('#typeAndOr').change(()=>{
	 			console.log("ddd")
	 			typeAndOr = $('#typeAndOr option:selected').val();
	 		
	 		}); 
	 		
	 		$('#sortType').change(()=>{
	 			
	 			var sortData = $('#sortType').val()
	 			$('#sortData').attr('disabled', false);
	 			
	 			if((sortData) === ""){
	 				$('#sortData').attr('disabled', true);
	 			}
	 		
	 		});
	 			
	 			
	 		
	 		// **** 추가 버튼의 인풋창 추가 기능!!!!!
		    var seq = 0;
		    $('#getText').click(()=>{
				
				console.log('클릭!');
				var creLabelKey = '';
				var creLabelValue = '';
				var creInput = '';
				$('#typeAndOr').attr('disabled',false);
				
				creLabelKey = '<div class="creDiv" id ="creDiv'+seq+'"><div class="col-sm-3"><label class= "control-label" style="padding-left:30px;" for="creValue'+seq+'">'+"키"+'</label>';
				creLabelValue = '<div class="col-sm-3"><label class= "control-label" style="padding-left:15px;" for="creValue'+seq+'">'+"값"+'</label>';
				creInput = creLabelKey+'<input type="text" style="width:170px; padding:6px 12px; margin-left:10px;"class = "form-control key" id = "creKey'+seq+'"></div>'+creLabelValue+'<input type="text" style="width:170px; padding:6px 12px; margin-left:11px;" class="form-control value" id = "creValue'+seq+'">'+'<i class="fas fa-backspace deleteBtn" style="padding-top:15px; margin-left:15px;"></i></div></div>'
				$('#createSearch').append(creInput);
				
				console.log("getText click event start!!!!!!!");
				var creIdKeys = $('#creKey').val();
				console.log(creIdKeys);
				console.log("getText click event start!!!!!!!");
				var creValues = $('#creValue').val();
				console.log(creValues);
				
				/* //동적태그에 이벤트를 걸어줄 때 -> 1. 이벤트, 2.동적태그 아이디, 3. 동작 함
				$(document).on("click","creKey", function(){
				}) */
				seq = seq+1;	
				//히든으로 뷰를append 하는 방법을 찾아보자
				//$('#dddd').val("add");

				//추가 버튼을 클릭하였을 때 인풋창에 추가 될 아이콘을 클릭했을 때! 
				$(".deleteBtn").on("click", function(){
		    		
					$(this).parent().parent().remove();
			
			    		seq = seq+1;
			    	});
			});
		    
		    $('#clearBtn').click(()=>{
		    
		    	console.log("clean click")
		    	$('#idSearch').val('');
		    	$(idKeySearch).val('');
		    	$(idValueSearch).val('');
		    	$('#sortType').val('');
		    	$('#sortType').attr('disabled',true);
		    	$('#sortData').val('');
		    	$("input[id^='creKey']").val('').remove();
		 		$("input[id^='creValue']").val('').remove();
		 		$("label[for^='creKey']").remove();
	    		$("label[for^='creValue']").remove();
	    		$(".deleteBtn").remove();
	    		$('#total').html('');
				$('#json').html('');		
				$('#typeAndOr').attr('disabled', true);
				$('#sortData').attr('disabled', true);
				$('#idSearch').attr('disabled', true);
		    });
		    
		    
		    $('#sizeData').focusout(()=>{
		    	
		    	console.log('this point!!!')
		    	var data = $('#sizeData').val();
		    	var regexp = /^[0-9]*$/

		    	
		    	if( !regexp.test(data) ) {

		    		alert("숫자만 입력하세요");

		    		$('#sizeData').focus();
		    		$('#sizeData').val('10');
		    		
		    	}
		    	console.log(data)
		    	if(data == '0'){
		    		alert('0이상으로 입력해 주시기 바랍니다.')
		    		$('#sizeData').focus();
		    		$('#sizeData').val('10');
		    	}

		    });
		    
		    	$('#SearchStart').click(()=>{
		    		/* 1. 밸리데이션 체크
		    		    - 인덱스 값이 없음 if 문으로
		    		    - type 체크하지 않음(체크가능)
		    		    - KeyValue 체크
		    		    1)키가 없거나 밸류가 없거나 (경고창 띄움);
		    	*/
				var key = document.getElementsByClassName('datakey');
		    	var value = document.getElementsByClassName('datavalue');
		    	var indexSearch = document.getElementById('indexList');
		    	var typeSearch =  document.getElementById('typeList');
		    	var idSearch = document.getElementById('idSearch');
		    	var index ="";
		    	var type = "";
		    	var id = "";
		    	var searchSize = "";
		    	var searchType = "";
		    	var sortType = "";
		    	var sortData = "";
			    var obj = {};
			    var config = {};
		    	var url = "startSearch";
		    	
		    	
		    	/*  if((indexSearch.value == "" || typeSearch.value == "" && indexSearch.value == null || typeSearch.value == null)){
		    		
		    		console.log('NoSearchIndex');
		    		alert("Index를 입력해 주시기 바랍니다");
		    		return;
		    	
		    	}else{
		    		console.log('dddd')
		    		index = indexSearch.value;
		    		type = typeSearch.value;
		    		config = $('#serverList').val();
		    		searchSize = $('#sizeData').val();
	    			
	    			sortType = $('#sortType').val();
	    			
	    			sortData = $('#sortData').val();
		    	} 
		    		
		    		
		    		if(indexSearch.value != "" && typeSearch.value != "" && idSearch.value != ""){
		    		
		    		console.log("인덱스 1")
		    		index = indexSearch.value;
		    		type = typeSearch.value;
		    		id = idSearch.value;
		    		config = $('#serverList').val();
		    		searchSize = $('#sizeData').val();
	    			
	    			sortType = $('#sortType').val();
	    			
	    			sortData = $('#sortData').val();
		    	
		    	}else if(indexSearch.value != "" || typeSearch.value != ""){
	 
		    		index = indexSearch.value;
		    		type = typeSearch.value;
		    		config = $('#serverList').val();
		    		searchSize = $('#sizeData').val();
	    			
	    			sortType = $('#sortType').val();
	    			
	    			sortData = $('#sortData').val();
	    			console.log('value???' + value);
	    			if(value != ""){
	    				index = indexSearch.value;
			    		type = typeSearch.value;
			    		config = $('#serverList').val();
			    		searchSize = $('#sizeData').val();
		    			sortType = $('#sortType').val();
		    			sortData = $('#sortData').val();
		    			
	    			}
	    				
 				}else if(indexSearch.value != "" && typeSearch.value != "" && idSearch.value != ""){
 					index = indexSearch.value;
		    		type = typeSearch.value;
		    		id = idSearch.value;
		    		config = $('#serverList').val();
		    		searchSize = $('#sizeData').val();
	    			
	    			sortType = $('#sortType').val();
	    			
	    			sortData = $('#sortData').val();
 				} 
		    		if($('#idValueSearch').val() != null || $('#idValueSearch').val() != ""){
		    		
		    			var searchQuery = new Map();
				    	var ids = new Array();
				    	var values = new Array();
				    	for(var i = 0; i<key.length; i++){
				    		//obj.idkey[i] = key[i].value;
				    	 	ids[i] = key[i].value;
				    	// 	console.log("key" + key[i].value);
				    	 	values[i] = value[i].value;
				    	//	console.log("value" + value[i].value);
				    	}
		    		} */
		    		
				console.log("searchStart!!!")		    	
		    	if(indexSearch.value == "" || typeSearch.value == "" && indexSearch.value == null || typeSearch.value == null){
		    		console.log('NoSearchIndex');
		    		alert("Index를 입력해 주시기 바랍니다");
		    		return;
		    		
		    	}else{
		    		
		    		console.log("인덱스1");
		    			index = indexSearch.value;
		    			
		    			type = typeSearch.value;
		    			
		    			config = $('#serverList').val();
		    			
		    			searchSize = $('#sizeData').val();
		    			
		    			sortType = $('#sortType').val();
		    			
		    			sortData = $('#sortData').val();
		    	}
		    	
		    	if(indexSearch.value != "" && typeSearch.value != "" && idSearch.value == "" ) {
				    //		console.log("idSearch Start!!!!");
				    
				    		console.log("인덱스2");
				    		index = indexSearch.value;
			    			
			    			type = typeSearch.value;
			    			console.log('type값?' + type);
			    			
			    			searchSize = $('#sizeData').val();
			    			
			    			config = $('#serverList').val();
			    			
			    			sortType = $('#sortType').val();
			    			sortData = $('#sortData').val();
				    	}
		    	 
		    	if(indexSearch.value != "" && typeSearch.value != "" && idSearch.value != "") {
		    //		console.log("idSearch Start!!!!");
		    
		    		console.log("인덱스3");
		    		index = indexSearch.value;
	    			
	    			type = typeSearch.value;
	    			
	    			id = idSearch.value;
	    			
	    			config = $('#serverList').val();
		    	}
		    	
		    	if(key[0].value != "" || value[0].value != ""){
		    		console.log("인덱스4"); 
		    		
		    	 if(typeAndOr.value == null && dataCheck.value == null){ 
		    		 console.log("인덱스5");
		    			index = indexSearch.value;
		    			type = typeSearch.value;
		    			sortType = $('#dataCheck option:selected').val();
		    			searchType = $('#typeAndOr option:selected').val();
		    			searchSize = $('#sizeData').val();
		    			sortType = $('#sortType').val();
		    			sortData = $('#sortData').val();
		    			
		    			console.log("searchSize" + searchSize);
		    		}
		    			
		    	}	
		    	if((indexSearch.value != "" && typeSearch.value != "" && idSearch.value != "") 
		    			&& (key[0].value != "" && value[0].value != "" && searchSize != "")){
		    		console.log("인덱스6");
		    		alert("아이디를 지워주시기 바랍니다!!!!");
		    		return false;
		    	}
		    	if(('#sizeData').value === '' || ('#sizeData').value === null){
		    		console.log("인덱스7");
		    		alert('사이즈를 입력해 주시기 바랍니다.');
		    		$('#sizeData').focus();
		    		$('#sizeData').val('10');
		    		
		    	}
		    	
		    		
		    	// key 배열의 변수가 1개이고 밸류 배열의 변수가 한개 일 때!! 
		    	// 반대의 경우를 생각해야 함.
		    	// 아래 예제는 1개를 뜻하기 떄문에 0 이나 -1은 될수가 없다. 
		    	// 반대의 경우는 1개 이상이다.
		    	// 초기 디폴트 input의 key 와 value의 값은 없어도 된다는 가정하에 조건식을 걸어 둔 것.(인덱스만 검색할 때, 인덱스 타입을 검색 할 때)
		    	// 두 값이 모두 입력이 되어도 콘솔로그를 찍는다. 다음 단계로 가지 않는다는 얘기.
		    	// 정리 : 기준을 디폴트 input의 배열의 길이가 1일때 (배열 변수는1개이상 있다)true, else문 이후엔 추가버튼을 눌렀을 때 생성되는 input창의 모든 밸류값을 조건식으로 넣어주었고,
		    	//	     true 일때 if문을 추가하여 디폴트 input의 키 밸류의 값이 한개씩만 들어갔을 때 조건식을 달아 주었다.
		    	
		    	if(key.length == 1 && value.length == 1) {
		    		console.log("인덱스8");
		    		console.log("key!" + key.length)
		    		//초기 디폴트 input의 key와 value 중 한개씩만 들어 갈 때의 조건식 키값O 밸류값X or 키값X 밸류값O
		    	 /* 	if((key[0].value == "" && value[0].value != "") || (key[0].value != "" && value[0].value == "")){
		    			alert("값을 입력해주시기 바랍니다.");
		    			return;
		    		}  */
		    	}else{ //key 배열의 변수가 1개이고 밸류 배열의 변수가 한개가 아닐 때!!
		    		console.log("인덱스9");
		    		console.log("key!!!" + key.length)
			    	for(var i = 0; i<key.length; i++){
			    		//key[i]밸류 값이 공백이거나 밸류[i]의 밸류값 값이 공백일 때
			    		//반대의 경우를 생각해야 함
			    		// -> 여기서 반대는 key가 포문을 돌기떄문에 전체 값이 잡히고 그 값의 밸류값이 입력이 됬을 때.
			    		 if(key[i].value == "" || value[i].value == ""){
			    			alert("kkk값을 입력해주시기 바랍니다.");
			    			return ;
			    		} 	
			    		
			    	}
		    	}
		    	
		    	var searchQuery = new Map();
		    	var ids = new Array();
		    	var values = new Array();
		    	for(var i = 0; i<key.length; i++){
		    		//obj.idkey[i] = key[i].value;
		    	 	ids[i] = key[i].value;
		    	// 	console.log("key" + key[i].value);
		    	 	values[i] = value[i].value;
		    	//	console.log("value" + value[i].value);
		    		
		    	} 
		    	
		    		$.ajax({
		                url :'/startSearch',
		                type :'POST',
		                data :{index : index,
		                	  type : type,
		                	  id : id,
		                	  idkey : ids, 
          			  		  idvalue : values,
          			  		  config : config,
          			  		  searchType : searchType,
          			  		  sortType : sortType,
          			  		  searchSize : searchSize,
          			  		  sortData : sortData},
          			  		  
		                dataType : 'json',
		                traditional : true,
		                success : function(data){
		                			//제이슨 이쁘게 출력하기
		                			/* JSON.parse() : parse 메소드는 객체를 json 객체로 변환시켜 줍니다.
		                			JSON.stringify : stringify 메소드는 json 객체를 string 객체로 변환시켜 줍니다. 
		                			+JSON.stringify(data, null, 4)
		                			*/ 
		                			console.log(data)
		                			if(data.totalData == null && data.totalData == undefined){
		                				console.log("idSearch Start");
		                			}else{
		                				
		                				totalData = JSON.stringify(data.totalData[0].datas.totalSearchData, null, 4);
		                				console.log('totaldata???' + totalData);
		                				searchData = JSON.stringify(data.totalData[0].datas.searchData, null, 4);
		                				//console.log(total)
		                				
		                				/* var total1 = String(data.totalData[0].datas);
		                				console.log(total1) */
		                				var labels = '<label class="control-label" style="margin:0px; padding:0px; color:blue;" for="total">totalData : '+totalData+'</label>'
		                				+'<label class="control-label" style="margin:0px 0px 0px 10px; padding:0px; color:blue;" for="total">searchData : '+searchData+'</label>'
		                				
		                			}
		                			
		                	for(var key in data){
		                	
		                	}
		                	
                			//console.log(total.value)
		                	$('#total').html(labels);
			    			//var html ='<pre>'+JSON.stringify(data[key], null, 4)+'</pre>'
							$("#json").jJsonViewer(data); // 타입 리스트에 연결 html 변수기능을 넣어 준다.
							
		                }
		            });
		    		
		    	}); 
 });
	 
	</script>


</body>
</html>


























