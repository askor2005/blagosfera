package ru.radom.blagosferabp.activiti.service;

import org.activiti.engine.repository.Deployment;

/**
 * Created by alex on 29.09.2015.<br/>
 * TODO comment me
 */
public interface DeploymentService {

    Deployment deployModel(String modelSource);
}
