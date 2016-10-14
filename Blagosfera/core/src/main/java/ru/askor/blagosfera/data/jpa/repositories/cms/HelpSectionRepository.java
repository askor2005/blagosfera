package ru.askor.blagosfera.data.jpa.repositories.cms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.askor.blagosfera.data.jpa.entities.cms.HelpSectionEntity;

import java.util.List;

/**
 * Created by vtarasenko on 04.04.2016.
 */
public interface HelpSectionRepository extends JpaRepository<HelpSectionEntity,Long>{
    @Query("select case when count(hs) > 0 then true else false end from HelpSectionEntity hs where hs.name = :name")
    public boolean checkExists(@Param("name") String name);
    @Query("select case when count(hs) > 0 then true else false end from HelpSectionEntity hs where hs.name = :name and hs != :exclude")
     boolean checkExists(@Param("name") String name,@Param("exclude") HelpSectionEntity exclude);
     HelpSectionEntity findByNameAndPublished(String name,boolean published);
     List<HelpSectionEntity> findByParent(HelpSectionEntity parent);
     List<HelpSectionEntity> findByParentAndPublished(HelpSectionEntity parent,boolean published);
     HelpSectionEntity findByName(String name);
}
