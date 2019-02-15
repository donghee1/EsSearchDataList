<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.3/css/all.css" integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/" crossorigin="anonymous">
<!-- <link rel="stylesheet" type="text/css" href="chrome-extension://chklaanhfefbnpoihckbnefhakgolnmc/jsonview-core.css"> -->
<link rel="stylesheet" href="./../css/contents.css?ver=4" type="text/css">
<!-- BootStrap CDN -->
<script src="https://code.jquery.com/jquery-3.3.1.js" integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60=" crossorigin="anonymous"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<!-- <script src="src/main/webapp/WEB-INF/views/fakeLoader/fakeLoader.min.js"></script>
<link rel="stylesheet" href="/src/main/webapp/WEB-INF/views/fakeLoader/fakeLoader.css">
 -->
<title>엘라스틱 서치</title>
</head>
<body>
	<div class="row" id = row>
		<div class="col-md-4 row" >
					<div class="col-sm-9">
					<label for="serverList" class="control-label two">서버</label>
						<select id="serverList" name="file_name" class="form-control">
							<option value="">선택하세요.</option>
							<option value="dev">dev</option>
							<option value="bmt">bmt</option>
						</select>
					</div>
					<!-- onchange 사용해보기  -->
					<!-- 포이치문에 인덱스 값을 생성하여 각 리스트마다 색인을 넣어주고 그 값으로 이벤트로 발생시킬 때 varStatus 사용하자!!! -->
					<div class="col-sm-9">
					<label for="indexList" class="control-label">인덱스</label>
						<select id="indexList" class="form-control" disabled="disabled">
							<option value="indexListOption">선택하세요.</option>
							<c:forEach var="row" items="${data}" varStatus="status">
								<option value="${row}" id="${status.index}"><c:out
										value="${row}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="col-sm-9">
					<label for="typeList" class="control-label two">타입</label>
						<select id="typeList" class="form-control" disabled="disabled">
							<option value="">선택하세요.</option>
						</select>
					</div>
					<div class="col-sm-9">
					<label for="indexSearch" class="control-label">인덱스</label>
						<input class="form-control" type="text" id="indexSearch"
							placeholder="인덱스 입력창">
					</div>
					<div class="col-sm-9">		
					<label for="typeSearch" class="control-label two">타입</label>
						<input class="form-control" type="text" id="typeSearch"
							placeholder="타입 입력창">
						</div>
						<div class="col-sm-10">
					<label for="idSearch" class="control-label">아이디</label>
						<input class="form-control" type="text" id="idSearch"
							placeholder="아이디 입력창">
						<button id="SearchStart" type="button" class="btn btn-primary">검색</button>
						<button id="clearBtn" type="button" class="btn btn-primary">클리어</button>
						</div>
					<div class="col-sm-5">
						<label for="typeAndOr" class="control-label four">검색조건</label>
							<select id=typeAndOr class="form-control" disabled="disabled">
								<option value="">선택하세요.</option>
								<option value="and">and</option>
								<option value="or">or</option>
							</select>
						</div>
						 <div class="col-sm-5">
						<label for="sizeData" class="control-label" style="">사이즈</label>
						<input type="number" id="sizeData" class="form-control size" min="1" value="10">
						</div>
						<!--  <label for="dataCheck">정렬 조건</label>
					<select id = dataCheck>
						<option value="">선택하세요.</option>
						<option value="ASC">오름차순</option>
						<option value="DESC">내림차순</option>
					</select> -->
					<div class="col-sm-12" id = "btn-group">
						<div class="col-sm-3">
						<label for="idKeySearch" id="labelKey" class="control-label one">키</label>
							<input type="text" id="idKeySearch" class="form-control key">
						</div>
						<div class="col-sm-4">
						<label for="idValueSearch" id="labelValue" class="control-label one">값</label>
							<input type="text" id="idValueSearch" class="form-control value">
							<button id="getText" type="button" class="btn btn-warning">추가</button>
						</div>
						<div class="col-sm-12" id="createSearch"></div>
						<div class="col-sm-4" id="search-Btn">
						</div>
					</div>
				</div>
		<div id='total'></div>
		<div id = 'json'class="col-md-8">
		
		</div>
		
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
 $(document).ready(function(){

	//index selectbox 데이터를 변경할 때
	 $('#indexList').change(() => {	
	 
	 	var getIndex = {}; //추가 
	 	var config = {};
	 	getIndex = $('#indexList option:selected').val(); // 인덱스
	 	config = $('#serverList option:selected').val();
	 	// index 셀렉트 박스 데이터 선택 후 디폴트 값을 선택 했을 때.
	 	if(getIndex === "indexListOption") {
	 		alert("다시 선택해 주시기 바랍니다.");
	 		$(indexSearch).val('');
	 		$(typeSearch).val('');
	 		$(idKeySearch).val('');
	 		$(idValueSearch).val('');
	 		$(sizeData).val('');
	 		$('#json').html('');
	 		$('#total').remove();
	 		var defualt = "<option>"+"선택하세요"+"</option>";
	 		$('#typeList').html('').append(defualt);
	 		$("input[id^='creKey']").remove();
	 		$("input[id^='creValue']").remove();
	 		$("label[for^='creKey']").remove();
    		$("label[for^='creValue']").remove();
	 		return false;
	 	}
	 	
		 $.post(`${location.origin}/typeList`, {
			
				getIndex : getIndex, //서버에 보낼 변수명
				config : config
		 
		}, (result) => {
			$('#typeList').attr('disabled',false);
			$('#indexSearch').val(getIndex);
			$('#typeList').empty();
			
			// 인풋창의 데이터를 초기화 해준다.
			$('#typeSearch').val('');
			$('#idKeySearch').val('');
			$('#idValueSearch').val('');
			$("input[id^='creKey']").val('');
			$('#json').html('');
			
			var defualt = "<option>"+"선택하세요"+"</option>";
	 		$('#typeList').append(defualt);
	 		
				$('#typeList').click(()=>{
					$('#typeList').html("").append(defualt);
					for(var i = 0; i < result.length; i++){
						var html ="<option>"+result[i]+"</option>";
					
					$("#typeList").append(html); // 타입 리스트에 연결 html 변수기능을 넣어 준다.	
					
					}
				})
			
			var getType = $('#typeList option:selected').val();
			
			
		}, 'json');
	 	
	 });
	
	 		//type 셀렉트 박스의 데이터가 변경 될 때
		    $('#typeList').change(() => {
		    	
		    	var IndexList = $('#indexList').val();
		    	var getType = $('#typeList option:selected').val();
				$('#typeSearch').val(getType);
						
		    });	
	 		
	 		$('#idList').change(() => {
	 			
	 		var IdLIst = $('#idList').val();
	 		
	 		var getId = $('#idList option:selected').val();
	 		$('#idSearch').val(getId);
	 		
	 		
	 		});
	 		
	 		$('#serverList').change(()=>{
	 		
	 		var config = $('#serverList option:selected').val();
	 		var defualt = "<option>"+"선택하세요"+"</option>";	
	 			$(indexSearch).val('');
	 			$(typeSearch).val('');
		 		$(idKeySearch).val('');
		 		$(idValueSearch).val('');
		 		$('#json').html('');
		 		$('#total').html('');
		 		$('#typeList').html('').append(defualt);
		 		
		 		if(config === "") {
		 		alert("다시 선택해 주시기 바랍니다.");
		 		$(indexSearch).val('');
		 		$(typeSearch).val('');
		 		$(idKeySearch).val('');
		 		$(idValueSearch).val('');
		 		$(sizeData).val('');
		 		$('#json').html('');
		 		$('#indexList').html('').append(defualt);
		 		$('#typeList').html('').append(defualt);
		 		$("input[id^='creKey']").remove();
		 		$("input[id^='creValue']").remove();
		 		$("label[for^='creKey']").remove();
	    		$("label[for^='creValue']").remove();
		 			
		 		return false;
		 	}
	 			
	 		$.get(`${location.origin}/checkServer`, {
	 				
				config :config
			 
			}, (result) => {
				$('#indexList').attr('disabled',false);
				$('#indexList').empty();
				var defualt = "<option>"+"선택하세요"+"</option>";
		 		$('#indexList').append(defualt);
				
				for(var i = 0; i < result.length; i++){
					
					var html ="<option>"+result[i]+"</option>";
					
					$('#indexList').append(html);
				}
					
	 			
	 		},"json");
	 		
	 		});
		      
	 		$('#typeAndOr').change(()=>{
	 			console.log("ddd")
	 			typeAndOr = $('#typeAndOr option:selected').val();
	 		
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
				creLabelValue = '<div class="col-sm-3"><label class= "control-label" style="padding-left:25px;" for="creValue'+seq+'">'+"값"+'</label>';
				creInput = creLabelKey+'<input type="text" style="width:170px; padding:0px; margin-left:10px;"class = "form-control key" id = "creKey'+seq+'"></div>'+creLabelValue+'<input type="text" style="width:170px; padding:0px; margin-left:2px;" class="form-control value" id = "creValue'+seq+'">'+'<i class="fas fa-backspace deleteBtn" style="padding-top:15px; margin-left:15px;"></i></div></div>'
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
		    	$("input[id^='creKey']").val('').remove();
		 		$("input[id^='creValue']").val('').remove();
		 		$("label[for^='creKey']").remove();
	    		$("label[for^='creValue']").remove();
	    		$(".deleteBtn").remove();
	    		$('#total').hide();
				$('#json').hide();		
				$('#typeAndOr').attr('disabled', true);
		    });
		    
		    
		    $('#sizeData').focusout(()=>{
		    	
		    	console.log('this point!!!')
		    	var data = $('#sizeData').val();
		    	var regexp = /^[0-9]*$/

		    	console.log('data?? ' + data);
		    	
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
		    	
				var key = document.getElementsByClassName('key');
		    	var value = document.getElementsByClassName('value');
		    	var indexSearch = document.getElementById('indexSearch');
		    	var typeSearch = document.getElementById('typeSearch');
		    	var idSearch = document.getElementById('idSearch');
		    	var index ="";
		    	var type = "";
		    	var id = "";
		    	var searchSize = "";
		    	var searchType = "";
		    	var sortType = "";
			    var obj = {};
			    var config = {};
		    	var url = "startSearch";
		    	
		    	
		    	if(indexSearch.value == "" || typeSearch.value == "" && indexSearch.value == null || typeSearch.value == null){
		    		console.log('NoSearchIndex');
		    		alert("Index를 입력해 주시기 바랍니다");
		    		return;
		    		
		    	}else{
		    			index = indexSearch.value;
		    			
		    			type = typeSearch.value;
		    			
		    			config = $('#serverList option:selected').val();
		    			
		    			searchSize = $('#sizeData').val();
		    	}
		    	
		    	if(indexSearch.value != "" && typeSearch.value != "" && idSearch.value == "" ) {
				    //		console.log("idSearch Start!!!!");
				    		index = indexSearch.value;
			    			
			    			type = typeSearch.value;
			    			
			    			searchSize = $('#sizeData').val();
			    			
			    			config = $('#serverList option:selected').val();
				    	}
		    	 
		    	if(indexSearch.value != "" && typeSearch.value != "" && idSearch.value != "") {
		    //		console.log("idSearch Start!!!!");
		    		index = indexSearch.value;
	    			
	    			type = typeSearch.value;
	    			
	    			id = idSearch.value;
	    			
	    			config = $('#serverList option:selected').val();
		    	}
		    	
		    	if(key[0].value != "" || value[0].value != ""){
		    		 
		    	 if(typeAndOr.value == null && dataCheck.value == null){ 
						
		    			index = indexSearch.value;
		    			type = typeSearch.value;
		    			sortType = $('#dataCheck option:selected').val();
		    			searchType = $('#typeAndOr option:selected').val();
		    			searchSize = $('#sizeData').val();
		    			
		    			console.log("searchSize" + searchSize);
		    		}
		    			
		    	}	
		    	if((indexSearch.value != "" && typeSearch.value != "" && idSearch.value != "") 
		    			&& (key[0].value != "" && value[0].value != "" && searchSize != "")){
		    		alert("아이디를 지워주시기 바랍니다!!!!");
		    		return false;
		    	}
		    	if(('#sizeData').value === '' || ('#sizeData').value === null){
		    		
		    		alert('사이즈를 입력해 주시기 바랍니다.');
		    		$('#sizeData').focus();
		    		$('#sizeData').val('1');
		    		
		    	}
		    	
		    		
		    	// key 배열의 변수가 1개이고 밸류 배열의 변수가 한개 일 때!! 
		    	// 반대의 경우를 생각해야 함.
		    	// 아래 예제는 1개를 뜻하기 떄문에 0 이나 -1은 될수가 없다. 
		    	// 반대의 경우는 1개 이상이다.
		    	// 초기 디폴트 input의 key 와 value의 값은 없어도 된다는 가정하에 조건식을 걸어 둔 것.(인덱스만 검색할 때, 인덱스 타입을 검색 할 때)
		    	// 두 값이 모두 입력이 되어도 콘솔로그를 찍는다. 다음 단계로 가지 않는다는 얘기.
		    	// 정리 : 기준을 디폴트 input의 배열의 길이가 1일때 (배열 변수는1개이상 있다)true else문 이후엔 추가버튼을 눌렀을 때 생성되는 input창의 모든 밸류값을 조건식으로 넣어주었고,
		    	//	     true 일때 if문을 추가하여 디폴트 input의 키 밸류의 값이 한개씩만 들어갔을 때 조건식을 달아 주었다.
		    	
		    	if(key.length == 1 && value.length == 1) {
		    		
		    		//초기 디폴트 input의 key와 value 중 한개씩만 들어 갈 때의 조건식 키값O 밸류값X or 키값X 밸류값O
		    	/* 	if((key[0].value == "" && value[0].value != "") || (key[0].value != "" && value[0].value == "")){
		    			alert("값을 입력해주시기 바랍니다.");
		    			return;
		    		} */
		    	}else{ //key 배열의 변수가 1개이고 밸류 배열의 변수가 한개가 아닐 때!!
		    		
			    	for(var i = 0; i<key.length; i++){
			    		//key[i]밸류 값이 공백이거나 밸류[i]의 밸류값 값이 공백일 때
			    		//반대의 경우를 생각해야 함
			    		// -> 여기서 반대는 key가 포문을 돌기떄문에 전체 값이 잡히고 그 값의 밸류값이 입력이 됬을 때.
			    		 if(key[i].value == "" || value[i].value == ""){
			    			alert("값을 입력해주시기 바랍니다.");
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
		    	
		    //	$ajax(`${location.origin}/startSearch`,{index : index, type : type, idkey : ids, idvalue : values}, (result) => {

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
          			  		  searchSize : searchSize},
          			  		  
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
		                				
		                				totalData = $('#total').show();
		                		    	searchData = $('#json').show();
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
			    			var html ='<pre>'+JSON.stringify(data[key], null, 4)+'</pre>'
							$("#json").html(html); // 타입 리스트에 연결 html 변수기능을 넣어 준다.

		                }
		            });
		    		
		    	}); 
 });
	 
	</script>


</body>
</html>


























