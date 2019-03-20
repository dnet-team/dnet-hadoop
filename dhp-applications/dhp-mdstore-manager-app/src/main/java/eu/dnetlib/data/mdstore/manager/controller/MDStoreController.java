package eu.dnetlib.data.mdstore.manager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.dnetlib.data.mdstore.manager.MDStoreManagerException;
import eu.dnetlib.data.mdstore.manager.model.MDStore;
import eu.dnetlib.data.mdstore.manager.model.MDStoreWithInfo;
import eu.dnetlib.data.mdstore.manager.model.Transaction;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreRepository;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreWithInfoRepository;
import eu.dnetlib.data.mdstore.manager.repository.TransactionRepository;

@RestController
@RequestMapping("/api/mdstores")
public class MDStoreController  {

	@Autowired
	private MDStoreRepository mdstoreRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private MDStoreWithInfoRepository mdstoreWithInfoRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public final Iterable<MDStoreWithInfo> find() {
		return mdstoreWithInfoRepository.findAll();
	}

	@RequestMapping(value = "/identifiers", method = RequestMethod.GET)
	public final List<String> findIdentifiers() {
		return mdstoreRepository.findAll().stream().map(MDStore::getId).collect(Collectors.toList());
	}

	@RequestMapping(value = "/byId/{id}", method = RequestMethod.GET)
	public final MDStoreWithInfo get(@PathVariable final String id) {
		return mdstoreWithInfoRepository.findById(id).orElse(null);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public final long count() {
		return mdstoreRepository.count();
	}

	@RequestMapping(value = "/new/{format}/{layout}/{interpretation}", method = RequestMethod.PUT)
	public MDStoreWithInfo  createMDStore(
			@PathVariable String format,
			@PathVariable String layout,
			@PathVariable String interpretation,
			@RequestParam(required=false) String dsId,
			@RequestParam(required=false) String apiId) throws MDStoreManagerException {
		final MDStore md = MDStore.newInstance(dsId, apiId, format, layout, interpretation);
		mdstoreRepository.save(md);

		final Transaction t = Transaction.newInstance(md.getId());
		t.setCurrent(true);
		transactionRepository.save(t);

		return mdstoreWithInfoRepository.findById(md.getId()).orElseThrow(() -> new MDStoreManagerException("MDStore not found"));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable final String id) throws MDStoreManagerException {
		if (transactionRepository.countByMdstoreAndActive(id, true) == 0) {
			transactionRepository.deleteByMdstore(id);
			mdstoreRepository.deleteById(id);
		} else {
			throw new MDStoreManagerException("Active transactions found on mdstore : " + id);
		}
	}

}
