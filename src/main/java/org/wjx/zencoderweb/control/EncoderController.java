package org.wjx.zencoderweb.control;

import org.springframework.web.bind.annotation.ResponseBody;
import org.wjx.zencoderweb.service.EncoderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.wjx.zencoderweb.zencoderkernel.PartitionerGenerator;
import org.wjx.zencoderweb.zencoderkernel.partitioner.Partitioner;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.*;


@Controller
public class EncoderController {

    private final EncoderService encoderService;
    List<String> files;

    public EncoderController(EncoderService encoderService) {
        this.encoderService = encoderService;
        files = new ArrayList<>();
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("operation", "encode");
        return "index";
    }

    @PostMapping("/process")
    public String process(
            @RequestParam(defaultValue = "") String input,
            @RequestParam(defaultValue = "pass") String requestaction,
            @RequestParam(defaultValue = "") String key,
            @RequestParam(defaultValue = "Null") String selectedFile,
            @RequestParam(required = false) Boolean aes,
            Model model) {

        String result = "";
        String operation = requestaction;


        switch (requestaction) {
            case "encode":
                if(!encoderService.setZencoder(selectedFile)) {
                    result = "无效的划分器";
                    break;
                }
                if(aes != null && aes){
                    try{
                        byte[] decodedKey = Base64.getDecoder().decode(key);
                        SecretKey enckey = new SecretKeySpec(decodedKey, "AES");
                        result = encoderService.encodeAES(input, enckey);
                    }
                    catch(Exception e){
                        result = "无效的密钥格式";
                    }

                }
                else{
                    result = encoderService.encode(input);
                }
                break;
            case "decode":
                if(!encoderService.setZencoder(selectedFile)) {
                    result = "无效的划分器";
                    break;
                }
                if(aes != null && aes){
                    try{
                        byte[] decodedKey = Base64.getDecoder().decode(key);
                        SecretKey enckey = new SecretKeySpec(decodedKey, "AES");
                        result = encoderService.decodeAES(input, enckey);
                    }
                    catch(Exception e){
                        result = "无效的密钥格式";
                    }

                }
                else{
                    result = encoderService.decode(input);
                }
                break;
            case "pass":
                files = encoderService.getFiles();
                break;
            // 其他case...
            default:
                operation = "invalid";
                result = "无效的操作类型";
        }


        model.addAttribute("input", input);
        model.addAttribute("result", result);
        model.addAttribute("key", key);
        model.addAttribute("aes", aes);
        model.addAttribute("fileList", files);
        model.addAttribute("selectedFile", selectedFile);
        model.addAttribute("operation", operation);
        return "index";
    }
    @GetMapping("/keygen")
    @ResponseBody
    public Map<String, String> generatePassword() {

        String password = encoderService.keygen();

        return Collections.singletonMap("mykey", password);
    }
}