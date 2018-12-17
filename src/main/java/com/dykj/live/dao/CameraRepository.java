package com.dykj.live.dao;


import com.dykj.live.pojo.Camera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByCdnContaining(String cdn);

    List<Camera> findByCdnidContaining(String cdn);
}
