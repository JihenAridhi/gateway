package com.uib.gateway.Services;

import java.sql.Date;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uib.gateway.Entities.ControlList;
import com.uib.gateway.Entities.User;
import com.uib.gateway.Repositories.ControlListRepository;

@Service
public class ControlListService {

    @Autowired
    ControlListRepository controlListRepository;


    public List<ControlList> findAll(Long id)
    {
        return controlListRepository.findAll();
    }

    public boolean match(User user)
    {
        List<ControlList> list = controlListRepository.findAll();
        int i = 0;
        while (!list.get(i).getName().toUpperCase().equals(user.getFirstName().toUpperCase()) && !list.get(i).getName().equals(user.getLastName())) {
            i++;
        }
        return i<list.size();
    }

    /* public boolean isMatch(Customer customer)
    {
        LevenshteinDistance ld = new LevenshteinDistance();
        int threshold = 2;

        List<ControlList> list = controlListRepository.findAll();

        int i = 0;
        boolean isMatch = false;
        while (!isMatch && i < list.size()) {
            int distance = ld.apply(list.get(i).getName().toUpperCase(), (customer.getName()));
            if (distance <= threshold) 
                isMatch = true;
            i++;
        }
        return i < list.size();
    } */

    public boolean nameMatch(List<String> aliases)
    {
        LevenshteinDistance ld = new LevenshteinDistance();
        int threshold = 2;

        List<ControlList> list = controlListRepository.findByType("INDIVIDUAL");
        for(String alias: aliases)
        {
            for(ControlList user: list)
            {
                int distance = ld.apply(alias, user.getName());
                if (distance <= threshold)
                    return true;
            }   
        }
        
        return false;
    }

    /* public boolean dateOfBirthMatch(Date dob)
    {
        List<ControlList> list = controlListRepository.findByType("INDIVIDUAL");
        for(ControlList user: list)
        {
            if (user.getDateOfBirth().equals(dob))
                    return true;
        }   
        
        return false;
    } */

    

}
