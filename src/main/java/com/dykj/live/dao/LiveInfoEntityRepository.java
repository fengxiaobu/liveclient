package com.dykj.live.dao;


import com.dykj.live.entity.LiveInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sjy
 */
public interface LiveInfoEntityRepository extends JpaRepository<LiveInfoEntity, Long> {
    /**
     * 根据CDN ID查询
     *
     * @param cdnid
     * @return
     */
    LiveInfoEntity findByCdnidContaining(String cdnid);

    LiveInfoEntity findByAppNameContaining(String appName);

    @Transactional(rollbackFor = Exception.class)
    int removeByAppName(String appName);

    boolean existsAllByAppName(String appName);

    boolean existsAllByCdnid(String cdnId);

}
