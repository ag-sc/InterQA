<!DOCTYPE html>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7"><![endif]-->
<!--[if IE 7]><html class="no-js lt-ie9 lt-ie8"><![endif]-->
<!--[if IE 8]><html class="no-js lt-ie9"><![endif]-->
<!--[if gt IE 8]><!--><html class="no-js"><!--<![endif]-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Selectize.js Demo</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/stylesheet.css">
    <link rel="stylesheet" href="css/selectize.css">
    <link href="http://www.jqueryscript.net/css/jquerysctipttop.css" rel="stylesheet" type="text/css">
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

    <script src="js/selectize.js"></script>
    <%--<script src="js/demo.js"></script>--%>


    <title>interQA Web App</title>
  </head>
  <body>

  <div id="wrapper">
    <h1>Proof of concept</h1>
    <div class="demo">
      <h2>Create a query by selecting elements (type in for a quick selection of elements) </h2>
      <div class="control-group">
        <pre id="log"></pre>
        <label for="select-state">NL query:</label>
        <select id="select-state" name="state[]" multiple class="demo-default" style="width:50%" placeholder="Select an element...">
          <option value="">Select an element...</option>
          <%@ page import = "interQA.*" %>
          <%@ page import="java.util.List" %>

          <%
            String fullPath = getServletContext().getRealPath("/") + "WEB-INF" + java.io.File.separator;
            String fileName1 = fullPath + "test1.rdf";
            String fileName2 = fullPath + "test2.rdf";
            QueryPatternManager qm = new QueryPatternManager();
            qm.addQueryPattern(new QueryPattern1(fileName1));
            //qm.addQueryPattern(new QueryPattern2(fileName2));
            List<String> opts = qm.getUIoptions();
            for (String opt : opts){
          %>
          <option value="<%= opt  %>"><%= opt  %></option>

          <%
            }
          %>
          <%--<option value="AAL">Allaabama</option>
          <option value="AK">Alaska</option>
          <option value="AZ">Arizona</option>
          <option value="AR">Arkansas</option>
          <option value="CA" selected>California</option>
          <option value="CO">Colorado</option>--%>
        </select>
      </div>
      <script>


        var eventHandler = function(name) {
          return function() {
            console.log(name, arguments);
            $('#log').append('<div><span class="name">' + name + '</span></div>');
          };
        };
        var $select = $('#select-state').selectize({
          create          : true,
          onChange        : eventHandler('onChange'),
          onItemAdd       : eventHandler('onItemAdd'),
          onItemRemove    : eventHandler('onItemRemove'),
          onOptionAdd     : eventHandler('onOptionAdd'),
          onOptionRemove  : eventHandler('onOptionRemove'),
          onDropdownOpen  : eventHandler('onDropdownOpen'),
          onDropdownClose : eventHandler('onDropdownClose'),
          onFocus         : eventHandler('onFocus'),
          onBlur          : eventHandler('onBlur'),
          onInitialize    : eventHandler('onInitialize'),
        });
      </script>
    </div>
  </div>
  </body>
</html>
