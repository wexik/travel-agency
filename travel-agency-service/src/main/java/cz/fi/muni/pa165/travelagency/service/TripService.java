package cz.fi.muni.pa165.travelagency.service;

import cz.fi.muni.pa165.travelagency.entity.Trip;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Julius Stassik
 */


public interface TripService {
    
    
   /**
    * create Trip
    * 
    * @param trip
    * @return created Trip
    */ 
   public Trip create(Trip trip);
   
   /**
    * update trip
    * 
    * @param trip which will be updated
    */
   public void update(Trip trip);
   
   /**
    * delete trip
    * 
    * @param trip which will be deleted 
    */
   public void delete(Trip trip);
   
   /**
    * find trip by its id
    * 
    * @param id - id of trip which has to be found
    * @return trip which was found 
    */
   public Trip getById(Long id);
   
   /**
    * find all trips
    * 
    * @return list of all trips
    */
   public List<Trip> getAll();
    
}
