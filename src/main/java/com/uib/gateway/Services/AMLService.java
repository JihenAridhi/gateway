package com.uib.gateway.Services;

import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uib.gateway.Controllers.CustomerController;
import com.uib.gateway.Entities.AML;
import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Repositories.AMLRepository;

@Service
public class AMLService {

    @Autowired
    AMLRepository amlr;


    public List<AML> findAllAML(Long id)
    {
        return amlr.findAll();
    }

    public boolean match(Customer customer)
    {
        List<AML> amlList = amlr.findAll();
        int i = 0;
        while (!amlList.get(i).getName().toUpperCase().equals(customer.getFirstName().toUpperCase()) && !amlList.get(i).getName().equals(customer.getLastName())) {
            i++;
        }
        return i<amlList.size();
    }

    public boolean isMatch(Customer customer)
    {
        LevenshteinDistance ld = new LevenshteinDistance();
        int threshold = 2;

        List<AML> amlList = amlr.findAll();

        int i = 0;
        boolean isMatch = false;
        while (!isMatch && i < amlList.size()) {
            int distance = ld.apply(amlList.get(i).getName().toLowerCase(), (customer.getFirstName()+" "+customer.getLastName()).toLowerCase());
            if (distance <= threshold) 
                isMatch = true;
            i++;
        }
        return i < amlList.size();
    }

}
