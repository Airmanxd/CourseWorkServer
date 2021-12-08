package com.example.coursework.storage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
@Component
public class MyResourceHttpRequestHandler extends ResourceHttpRequestHandler {

    public static String ATTR_FILE = MyResourceHttpRequestHandler.class.getName() + ".file";

    /**
     * returns file as Resource
     * @param request client request for the file
     * @return returns file as Resource
     * @throws IOException
     */
    @Override
    public Resource getResource(HttpServletRequest request) throws IOException {

        final File file = (File) request.getAttribute(ATTR_FILE);
        return new FileSystemResource(file);
    }
}
