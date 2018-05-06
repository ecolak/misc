package emre.colak.leftoverpolice.service;

import emre.colak.leftoverpolice.model.Leftover;

public interface ILeftoverService {

  void create(Leftover leftover);
  
  void delete(String id);
  
  Iterable<Leftover> list();
  
  Iterable<Leftover> listSortedByDateAdded(SortDir sortDir);
  
}
