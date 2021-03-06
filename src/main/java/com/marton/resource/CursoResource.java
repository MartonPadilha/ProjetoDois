package com.marton.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.marton.DTO.CursoDTO;
import com.marton.entity.CursoEntity;
import com.marton.service.CursoService;

@RestController
@RequestMapping(value="/cursos")
public class CursoResource {
	
	@Autowired
	CursoService service;
	
	
	
	@RequestMapping(method=RequestMethod.GET)
	public List<CursoDTO> listar() {
		List<CursoEntity> listaEntity = service.buscar();
		
		List<CursoDTO> listaDTO = listaEntity.stream().map(obj -> new CursoDTO(obj)).collect(Collectors.toList());
		return listaDTO;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ResponseEntity<CursoEntity> buscar(@PathVariable Integer id){
		CursoEntity objeto = service.buscar(id);
		return ResponseEntity.ok(objeto);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> salvar(@Valid @RequestBody CursoDTO objDTO){
		
		CursoEntity obj = new CursoEntity(
				null, 
				objDTO.getNome(), 
				objDTO.getNivel(), 
				objDTO.getTurno()
				);
		
		obj = service.salvar(obj);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		
		return ResponseEntity.created(uri).build();
	}
	//localhost:8080/cursos/1
	//Listar todos -> localhost:8080/cursos [GET]
	//lsitar um -> localhost:8080/cursos/1 [GET]
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Void> atualizar(@RequestBody CursoEntity obj, @PathVariable Integer id){
		obj.setId(id);
		obj = service.atualizar(obj);
		return ResponseEntity.noContent().build();//retorna 204 no servidor
	}
	
	
	@RequestMapping(value = "/paginacao", method = RequestMethod.GET)
	public ResponseEntity<Page<CursoDTO>> listarPaginas(
			
			@RequestParam(value="nome", defaultValue="") String nome,
			@RequestParam(value = "pagina", defaultValue = "0") Integer pagina, 
			@RequestParam(value = "qtd", defaultValue = "15") Integer qtdLinhas, 
			@RequestParam(value = "ordem", defaultValue = "nome") String orderBy, 
			@RequestParam(value = "dir", defaultValue = "DESC") String dir){
		
		Page<CursoEntity> listaCursos = 
				service.buscarPorPagina(nome, pagina, qtdLinhas, orderBy, dir);
		
		Page<CursoDTO> listaDTO = listaCursos.map(obj -> new CursoDTO(obj));
		
		return ResponseEntity.ok().body(listaDTO);
	}
	
	
}
