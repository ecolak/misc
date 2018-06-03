package emre.colak.leftoverpolice.service;

import java.util.List;

import emre.colak.leftoverpolice.model.Leftover;

public interface ILeftoverService {

  void create(Leftover leftover);
  
  void delete(int id);
  
  int deleteAll();
  
  List<Leftover> list();
  
  List<Leftover> listSortedByDateAdded(SortDir sortDir);
  
  List<String> listNames();
  
  List<Leftover> searchByName(String name);
  
}
