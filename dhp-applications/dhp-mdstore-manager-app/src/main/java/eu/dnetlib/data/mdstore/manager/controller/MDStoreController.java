package eu.dnetlib.data.mdstore.manager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.dnetlib.data.mdstore.manager.model.MDStore;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreRepository;

@RestController
@RequestMapping("/api/mdstores")
public class MDStoreController  {

	@Autowired
	private MDStoreRepository repo;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public final List<MDStore> find() {
		return repo.findAll();
	}

	@RequestMapping(value = "/identifiers", method = RequestMethod.GET)
	public final List<String> findIdentifiers() {
		return repo.findAll().stream().map(MDStore::getId).collect(Collectors.toList());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public final MDStore get(@PathVariable final String id) {
		return repo.findById(id).orElse(null);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public final long count() {
		return repo.count();
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public final void save(@RequestBody final MDStore entity) {
		repo.save(entity);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable final String id) {
		repo.deleteById(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public void update(@PathVariable final String id, @RequestBody final MDStore entity) {
		if (repo.existsById(id)) {
			entity.setId(id);
			repo.save(entity);
		}
	}

}
