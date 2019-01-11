<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content= "text/html; charset=UTF-8">
<!-- BootStrap CDN -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">
<title>게시글 작성</title>
</head>
<body>
    <h3>게시글 작성</h3>
    <div style="padding : 30px;">
    	<!-- <META http-equiv="refresh" content="1;url=localhost:8080/delete"> -->
        <form method="POST" action="/update1">
         <div class="form-group">
            <label>인덱스</label>
            <input type="text" name="index" class="form-control">
          </div>
           <div class="form-group">
            <label>타입</label>
            <input type="text" name="type" class="form-control">
          </div>
           <div class="form-group">
            <label>아이디</label>
            <input type="number" name="id" class="form-control">
          </div>
          <div class="form-group">
            <label>제목</label>
            <input type="text" name="title" class="form-control">
          </div>
          <div class="form-group">
            <label>작성자</label>
            <input type="text" name="name" class="form-control">
          </div>
          
          <div class="form-group">
            <label>내용</label>
            <textarea name="content" class="form-control" rows="5"></textarea>
          </div>
          <button type="submit" class="btn btn-default">변경</button>
        </form>
       
      <!--   <script type="text/javascript">
        window.location.href= "localhost:8080/delete"
        </script> -->
    </div>
</body>
</html>






