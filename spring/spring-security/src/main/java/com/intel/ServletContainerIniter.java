package com.intel;

import java.util.Set;


import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes(SecurityWebApplicationInitializer.class)
public class ServletContainerIniter implements ServletContainerInitializer{
    @Override
    public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
            throws ServletException {
        System.out.println("dsafadsf");
    }
}
