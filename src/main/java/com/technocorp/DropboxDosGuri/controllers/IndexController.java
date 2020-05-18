package com.technocorp.DropboxDosGuri.controllers;

import com.technocorp.DropboxDosGuri.armazenamento.ExcecaoArquivoNaoEncontrado;
import com.technocorp.DropboxDosGuri.armazenamento.ServicosArmazenamento;
import com.technocorp.DropboxDosGuri.security.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/home")
public class IndexController {

    private ServicosArmazenamento servicos;

    @Autowired
    HttpServletRequest request;

    private UserDetail user;
//    private final Path rootLocation;

    @Autowired
    public IndexController(ServicosArmazenamento servicos) {
        this.servicos = servicos;
//        this.rootLocation = Paths.get("upload-dir");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String telaHome(Model model){

        this.user = detalhaUsuario();

        model.addAttribute("arquivos", servicos.carregaTodos(user.getUsuario()).map(
                path -> MvcUriComponentsBuilder.fromMethodName(IndexController.class,
                        "entregaArquivo", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));


        return "home";
    }

    @GetMapping("/arquivos/{nomeArquivo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> entregaArquivo(@PathVariable String nomeArquivo){
        this.user = detalhaUsuario();
        Resource arquivo = servicos.carregaRecurso(nomeArquivo, user.getUsuario());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + arquivo.getFilename() + "\"").body(arquivo);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String salvou(@RequestParam("file") MultipartFile arquivo,
                         RedirectAttributes redirectAttributes){
        this.user = detalhaUsuario();

        servicos.armazena(arquivo, user.getUsuario().toString());

        return "redirect:/home";
    }

    @PostMapping("/arquivos/{nomeArquivo:.+}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String testeExclusaoPost(@PathVariable String nomeArquivo){
        this.user = detalhaUsuario();
        servicos.exclui(nomeArquivo, user.getUsuario());
        return "redirect:/home";
    }

    @DeleteMapping("/arquivos/{nomeArquivo:.+}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @ResponseBody
    public ResponseEntity<Resource> deletaArquivo(@PathVariable String nomeArquivo){
        this.user = detalhaUsuario();
        servicos.exclui(nomeArquivo, user.getUsuario());
        return ResponseEntity.noContent().build();
    }


    private UserDetail detalhaUsuario(){
        if (request != null){
            this.user = new UserDetail(request);
        }
        return user;
    }



    @ExceptionHandler(ExcecaoArquivoNaoEncontrado.class)
    public ResponseEntity<?> handleStorageFileNotFound(ExcecaoArquivoNaoEncontrado exc) {
        return ResponseEntity.notFound().build();
    }

}
