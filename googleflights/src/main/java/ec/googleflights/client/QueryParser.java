package ec.googleflights.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class QueryParser {

  /**
   * Example query:
   * [[[[[[null,[["IST",0]]],[null,[["FRA",0]]],["2019-10-13"],null],[[null,[["FRA",0]]],[null,[["IST",0]]],["2019-10-18"],null]],null,[1],null,null,null,null,1,null,2,null,null,null,null,null,0],[[[0,[],[],[],[],null,[],[]],[0,[],[],[],[],null,[],[],null,null]]],null,"USD",null,[]],[true]]
   * 
   */
  public static List<Object> parseQuery(String query) {
    Stack<List<Object>> stack = new Stack<>();
    List<Object> curList = null;
    StringBuilder elemsb = new StringBuilder();
    char prev = 0;
    for (char c : query.toCharArray()) {
      if (c == '[') {
        if (curList != null) {
          stack.push(curList);
        }
        curList = new LinkedList<Object>();
      } else if (c == ']') {
        if (!stack.isEmpty()) {
          if (elemsb.length() > 0) {
            curList.add(elemsb.toString());
            elemsb = new StringBuilder(); 
          }
          List<Object> list = stack.pop();
          list.add(curList);
          curList = list;
        }
      } else if (c == ',') {
        if (prev != ']') {
          curList.add(elemsb.toString());
          elemsb = new StringBuilder(); 
        } 
      } else {
        elemsb.append(c);
      }
      prev = c;
    }
    return curList;
  }

  public static void main(String[] args) {
    //String q =
    //"[[[[[[null,[[\"IST\",0]]],[null,[[\"FRA\",0]]],[\"2019-10-13\"],null],[[null,[[\"FRA\",0]]],[null,[[\"IST\",0]]],[\"2019-10-18\"],null]],null,[1],null,null,null,null,1,null,2,null,null,null,null,null,0],[[[0,[],[],[],[],null,[],[]],[0,[],[],[],[],null,[],[],null,null]]],null,\"USD\",null,[]],[true]]";
    //String q = "[7,[0,178,279],[46,5]]";
    //String q = "[7,[\"cat\",178,[279,\"bag\",[89,6]]],[46,5]]";
    //String q = "[[1,[2,4]],[3,[4,89,77]],[5,6]]";
    String q = "[[[[[[null,[[\"SFO\",0]]],[null,[[\"MUC\",0]]],[\"2019-10-07\"],null]],2,null,1,null,null,null,1,null,2,null,null,null,null,null,0,true],[[[1,[],[[12,22]],[],1200,[],[],null,null]]],null,\"USD\",null,[],null,null],[0]]";
    List<Object> list = parseQuery(q);
    System.out.println(list);
  }

}

