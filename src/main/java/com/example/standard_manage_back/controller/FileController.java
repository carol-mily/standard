package com.example.standard_manage_back.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.standard_manage_back.common.Result;
import com.example.standard_manage_back.entity.Files;
import com.example.standard_manage_back.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传相关接口
 */
@RestController
@RequestMapping("/file")
public class FileController {

    //引入文件存储路径
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    /**
     * 通过文件的md5查询文件
     *
     * @param md5
     * @return
     */
    private Files getFileByMD5(String md5) {
        //查询当前文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        Files files = fileMapper.selectOne(queryWrapper); //md5唯一，故可只查询一个
        return files;
    }

    /**
     * 文件上传接口
     *
     * @param file 前端上传的文件
     * @return 文件链接
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) {
        String originalFilename = file.getOriginalFilename(); //不使用原始后缀名，怕特殊字符等问题
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        //定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;  //将后缀添加上，在获取的时候会更方便
        File uploadFile = new File((fileUploadPath + fileUUID));
        //如果其父亲目录不存在，则新建目录
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();    //创建目录
        }
        //把获取到的文件存档磁盘目录
        try {
            file.transferTo(uploadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url;   //文件链接
        //获取文件的md5（磁盘文件的唯一标识），通过对比md5避免重复上传相同的文件
        String md5 = SecureUtil.md5(uploadFile);
        //从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMD5(md5);
        if (dbFiles != null) {  //文件重复，不需要存入数据库
            url = dbFiles.getUrl();
            uploadFile.delete();    //删除
        } else {
            url = "http://localhost:9090/file/" + fileUUID;
            //存储数据库
            Files saveFile = new Files();
            saveFile.setName(fileUUID); //重新命名
            saveFile.setType(type);
            saveFile.setSize(size / 1024);    //将文件大小单位转为kb
            saveFile.setUrl(url);
            saveFile.setMd5(md5);
            fileMapper.insert(saveFile);
        }
        return url;
    }

    /**
     * 文件下载接口   "http://localhost:9090/file/"+fileUUID
     *
     * @param fileUUID 传入标识位
     *                 以流方式返回
     */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        //根据文件的唯一标识码获取文件
        File uploadFile = new File((fileUploadPath + fileUUID));    //文件路径
        //设置输出流格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");
        //读取文件上传字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 分页查询---mybatis-plus方式
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/getPage")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name){
        QueryWrapper<Files> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("name",name);
        IPage<Files> page=new Page<>(pageNum,pageSize);
        return Result.success("获取成功",fileMapper.selectPage(page,queryWrapper));
    }

    /**
     * 更新(可更新name、enable)
     * @param files
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Files files){
        //更新
        return Result.success("更新成功",fileMapper.updateById(files));
    }


    /**
     * 删除单个（假删除）
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        Files files =fileMapper.selectById(id);
        files.setIsDelete(true);
        fileMapper.updateById(files);
        return Result.success("删除成功");
    }

    /**
     * 删除多个（假删除）
     * @param ids
     * @return
     */
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        //select * from sys_files where id in (id,id,id……)
        QueryWrapper<Files> queryWrapper=new QueryWrapper<>();
        //查询未删除记录
        queryWrapper.eq("is_delete",false);
        queryWrapper.in("id",ids);
        List<Files> files=fileMapper.selectList(queryWrapper);
        for(Files file :files){
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return Result.success("批量删除成功");
    }
}
