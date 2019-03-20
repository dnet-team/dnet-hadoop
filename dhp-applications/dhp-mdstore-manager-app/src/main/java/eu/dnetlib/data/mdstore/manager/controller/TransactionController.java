package eu.dnetlib.data.mdstore.manager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.dnetlib.data.mdstore.manager.model.Transaction;
import eu.dnetlib.data.mdstore.manager.repository.TransactionRepository;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController  {

	@Autowired
	private TransactionRepository repo;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public final List<Transaction> find() {
		return repo.findAll();
	}

	@RequestMapping(value = "/identifiers", method = RequestMethod.GET)
	public final List<String> findIdentifiers() {
		return repo.findAll().stream().map(Transaction::getId).collect(Collectors.toList());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public final Transaction get(@PathVariable final String id) {
		return repo.findById(id).orElse(null);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public final long count() {
		return repo.count();
	}
}
