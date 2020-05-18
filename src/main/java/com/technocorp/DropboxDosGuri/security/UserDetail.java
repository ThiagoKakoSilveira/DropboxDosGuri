package com.technocorp.DropboxDosGuri.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Singular;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Data
public class UserDetail {
    private HttpServletRequest request;

    private String usuario;
    private String nome;
    private String sobrenome;
    private String email;
    private String regiao;
    private AccessToken.Access acessoRegiao;
    private Set<String> permissoes;


    public UserDetail(HttpServletRequest request){
        if (request!=null){
            this.request = request;
            getInfos();
        }else{
            System.out.println("Loga que veio nula a request");
        }
    }

    private void setAtributes(){
        this.nome = getKeycloakSecurityContext().getIdToken().getGivenName();
        this.sobrenome = getKeycloakSecurityContext().getIdToken().getFamilyName();
//        this.nome = getKeycloakSecurityContext().getIdToken().getName();
    }

    private void configCommonAttributes(Model model) {
        model.addAttribute("name", getKeycloakSecurityContext().getIdToken().getGivenName());
    }

    public void getInfos(){
//        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
//        KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
//        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        KeycloakSecurityContext session = getKeycloakSecurityContext();

        AccessToken accessToken = session.getToken();
        this.usuario = accessToken.getPreferredUsername();
        this.email = accessToken.getEmail();
        this.sobrenome = accessToken.getFamilyName();
        this.nome = accessToken.getGivenName();
        this.regiao = accessToken.getIssuer();
        this.acessoRegiao = accessToken.getRealmAccess();
        this.permissoes = acessoRegiao.getRoles();
    }


    /**
     * The KeycloakSecurityContext provides access to several pieces of information
     * contained in the security token, such as user profile information.
     */
    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }
}
