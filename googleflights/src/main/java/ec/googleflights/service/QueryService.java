package ec.googleflights.service;

import ec.googleflights.model.Query;
import ec.googleflights.model.result.QueryResponse;

public interface QueryService {

  QueryResponse SendQuery(Query q);
}
