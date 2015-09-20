package com.swych.mobile.activity;

/**
 * Created by Manu on 9/19/2015.
 */
public class Scripts {
    // reader fields and controls.
    public  static final String CHAPTER = "CHAPTER";
    public static final String PARAGRAPH_TAG = "<P>";
    public static String CLICK_EVENT="onClick";
    public static String RENDER_EVENT="render";
    public static String templateFirstPartBackward = "<html>"+
            "<head> "+
            "<script src='jquery-2.1.3.min.js' type='text/javascript'></script>"+
            "</head>"+
            "<style type=\"text/css\">\n" +
            ".sentence_block.heading {\n" +
            "    text-align: center;\n" +
            "    font-size: 25px;\n" +
            "}\n" +
            ".sentence_block{\n" +
            "\tfont-size: 17px;\n" +
            "}\n" +
            ".sentence_block.mode2_block{\n" +
            "  font-size: 17px;\n" +
            "  font-style: italic;\n" +
            "  color: blue;\n" +
            "}" +
            "\n" +
            "</style>"+
            "<body>"+
            "<div id='page_content' align=\"justify\" style='visibility:hidden'>"+
            "<script>" +
            "$( document ).ready(function() {" +
            "var viewportHeight;\n" +
            "  if (document.compatMode === 'BackCompat') {\n" +
            "      viewportHeight = document.body.clientHeight;\n" +
            "  } else {\n" +
            "      viewportHeight = document.documentElement.clientHeight;\n" +
            "  }\n" +
            "viewportHeight=viewportHeight-10; \n"+
            "  cutoff=' ';\n" +
            "  rem_sentece_id='';\n" +
            "  var firstSpan;\n" +
            "  var removeSentences = true;\n" +
            "  var isTranslation = false; \n" +
            "  if(document.getElementById('page_content').offsetHeight < viewportHeight){\n" +
            "    alert('need_more')\n" +
            "  }\n" +
            "  else{"+
            "  while(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "      firstSpan =  $('#page_content span.sentence_block:first');\n" +
            "      if(removeSentences){\n" +
            "        $('p').each(function(index, item) {\n" +
            "            if($.trim($(item).text()) === \"\") {\n" +
            "                $(item).remove(); // $(item).remove();\n" +
            "            }\n" +
            "        });\n" +
            "\n" +
            "        firstSpan.detach();\n" +
            "        if(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "           continue;\n" +
            "        }\n" +
            "        removeSentences=false;\n" +
            "        if(firstSpan.hasClass(\"mode2_block\")){\n" +
            "            isTranslation=true;\n" +
            "        }"+
            "        rem_sentece_id = firstSpan.attr(\"data-sentence_id\");\n" +
            "        firstSpan.prependTo($('p:first'));}      \n" +
            "      var s = $('#page_content span.sentence_block:first').text();\n" +
            "      var pos = s.indexOf(' ');\n" +
            "\n" +
            "      if(pos>0){\n" +
            "           cutoff = cutoff + ' '+s.substr(0, pos);\n" +
            "           s = s.substr(pos+1,s.length);\n" +
            "      }\n" +
            "      else{\n" +
            "           cutoff = cutoff + ' '+s;\n" +
            "           s=\"\";\n" +
            "      }"+
            "      $('#page_content span.sentence_block:first').html(s);\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    alert('"+RENDER_EVENT+"' +'###' + cutoff +\"###\" + 'rem_sentece_id' + \"###\" + rem_sentece_id +\"###\"+'included_string'+\"### \"+ s +\"###translation###\" + isTranslation);}"+
            "});" +
            "$(document).on('click', '.mode2_block', function() {\n" +
            "  alert('"+CLICK_EVENT+":'+$(this).attr('data-sentence_id'));\n" +
            "});" +
            "</script>"+
            "<p>";
    public static String  templateFirstPartForward = "<html>"+
            "<head> "+
            "<script src='jquery-2.1.3.min.js' type='text/javascript'></script>"+
            "</head>"+
            "<style type=\"text/css\">\n" +
            ".sentence_block.heading {\n" +
            "    text-align: center;\n" +
            "    font-size: 25px;\n" +
            "}\n" +
            ".sentence_block{\n" +
            "font-size: 17px;\n" +
            "}\n" +
            ".sentence_block.mode2_block{\n" +
            "  font-size: 17px;\n" +
            "  font-style: italic;\n" +
            "  color: blue;\n" +
            "}"+
            "\n" +
            "</style>"+
            "<body>"+
            "<div id='page_content' align=\"justify\" style='visibility:hidden'>"+
            "<script>" +
            "$( document ).ready(function() {" +
            "cutoff=' ';\n" +
            "rem_sentece_id=' ';\n" +
            "var viewportHeight;\n" +
            "if (document.compatMode === 'BackCompat') {\n" +
            "    viewportHeight = document.body.clientHeight;\n" +
            "} else {\n" +
            "    viewportHeight = document.documentElement.clientHeight;\n" +
            "}" +
            "viewportHeight=viewportHeight-10; \n"+
            "    var lastSpan;\n" +
            "    var removeSentences = true;\n" +
            "    var isTranslation = false; \n" +
            "  if(document.getElementById('page_content').offsetHeight < viewportHeight){\n" +
            "    alert('need_more')\n" +
            "  }\n" +
            "  else{"+
            "    while(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "        lastSpan =  $('#page_content span.sentence_block:last');\n" +
            "        if(removeSentences){\n" +
            "        $('p').each(function(index, item) {\n" +
            "            if($.trim($(item).text()) === \"\") {\n" +
            "                $(item).remove(); // $(item).remove();\n" +
            "            }\n" +
            "        });\n" +
            "        lastSpan.detach();\n" +
            "        if(document.getElementById('page_content').offsetHeight > viewportHeight){\n" +
            "            continue;\n" +
            "        }\n" +
            "        removeSentences=false;\n" +
            "        if(lastSpan.hasClass(\"mode2_block\")){\n" +
            "            isTranslation=true;\n" +
            "        }"+
            "        rem_sentece_id = lastSpan.attr(\"data-sentence_id\");\n" +
            "        lastSpan.appendTo($('p:last'));\n" +
            "        }\n" +
            "        //remove maximum number of sentences;\n" +
            "\n" +
            "        \n" +
            "        var s = $('#page_content span.sentence_block:last').text();\n" +
            "        var pos = s.lastIndexOf(' ');\n" +
            "        cutoff = s.substr(pos+1, s.length) + ' ' +cutoff;\n" +
            "        s = s.substr(0,pos);\n" +
            "         $('#page_content span.sentence_block:last').html(s);\n" +
            "    }\n" +
            "\n" +
            "    alert('"+RENDER_EVENT+"' +'###' + cutoff +\"###\" + 'rem_sentece_id' + \"###\" + rem_sentece_id+\"###\"+'included_string'+\"### \"+ s +\"###translation###\" + isTranslation ); }" +
            "});\n" +
            "$(document).on('click', '.mode2_block', function() {\n" +
            "  alert('"+CLICK_EVENT+":'+$(this).attr('data-sentence_id'));\n" +
            "});" +
            "</script>"+
            "<p>";
    public static String LOW_BUFFER="need_more";
    public static String SENTENCE_FORMAT= "<span class='sentence_block' data-sentence_id='%s'>%s</span>" ;
    public static String MODE2_FORMAT = "<span class='sentence_block mode2_block' data-sentence_id='%s'>%s</span>";
    public static String PARAGRAPH_FORMAT="</p>\n<p> ";
    public static String CHAPTER_FORMAT = "<span class='sentence_block heading' data-sentence_id='%s'>%s</span> \n";
    public static String templateSecondPart = "</p> " +
            "</div></body></html>";
}
