/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.model.manual.repository;

import com.clarolab.dto.db.ComponentsDTO;
import com.clarolab.model.manual.ProductComponent;
import com.clarolab.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductComponentRepository extends BaseRepository<ProductComponent> {

    List<ProductComponent> findAllByEnabled(boolean enabled);

    @Query("SELECT p FROM ProductComponent p WHERE LOWER (name) like ?1 AND enabled = true ORDER BY name")
    List<ProductComponent> search(String name);

    List<ProductComponent> findAllByNameIgnoreCaseLikeAndEnabled(String name, boolean enabled);

    @Query("SELECT distinct p FROM ProductComponent p INNER JOIN ManualTestCase m ON p.id = m.component1 ORDER BY p.name")
    List<ProductComponent> search1();

    @Query("SELECT new com.clarolab.dto.db.ComponentsDTO(m.component1, m.component2, m.component3) from ManualTestCase m where m.component1 = ?1 OR m.component2 = ?1 OR m.component3 = ?1" )
    List<ComponentsDTO> search(ProductComponent compo1);

    @Query("SELECT new com.clarolab.dto.db.ComponentsDTO(m.component1, m.component2, m.component3) from ManualTestCase m where (m.component1 = ?1 AND m.component2 = ?2) OR (m.component1 = ?2 AND m.component2 = ?1) OR (m.component1 = ?1 AND m.component3 = ?2) OR (m.component3 = ?1 AND m.component1 = ?2) OR (m.component2 = ?1 AND m.component3 = ?2) OR (m.component3 = ?1 AND m.component2 = ?2) " )
    List<ComponentsDTO> search(ProductComponent compo1, ProductComponent compo2);

}
