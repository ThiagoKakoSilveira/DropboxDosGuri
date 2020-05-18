package com.technocorp.DropboxDosGuri.armazenamento;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("storage")
public class ArmazenamentoProperties {

	private String local = "memoria";

}
