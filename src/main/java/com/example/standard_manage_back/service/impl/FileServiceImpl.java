package com.example.standard_manage_back.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard_manage_back.entity.Files;
import com.example.standard_manage_back.mapper.FileMapper;
import com.example.standard_manage_back.service.IFileService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author carol
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {

}
