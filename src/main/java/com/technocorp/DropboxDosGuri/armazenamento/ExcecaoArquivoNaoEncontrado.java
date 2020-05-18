package com.technocorp.DropboxDosGuri.armazenamento;

public class ExcecaoArquivoNaoEncontrado extends ExcecaoArmazenamento {

    public ExcecaoArquivoNaoEncontrado(String mensagem) {
        super(mensagem);
    }

    public ExcecaoArquivoNaoEncontrado(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
