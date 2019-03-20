package eu.dnetlib.data.mdstore.manager.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.dnetlib.data.mdstore.manager.exceptions.MDStoreManagerException;
import eu.dnetlib.data.mdstore.manager.exceptions.NoContentException;
import eu.dnetlib.data.mdstore.manager.model.MDStore;
import eu.dnetlib.data.mdstore.manager.model.MDStoreCurrentVersion;
import eu.dnetlib.data.mdstore.manager.model.MDStoreVersion;
import eu.dnetlib.data.mdstore.manager.model.MDStoreWithInfo;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreCurrentVersionRepository;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreRepository;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreVersionRepository;
import eu.dnetlib.data.mdstore.manager.repository.MDStoreWithInfoRepository;

@RestController
@RequestMapping("/mdstores")
public class MDStoreController {

	@Autowired
	private MDStoreRepository mdstoreRepository;
	@Autowired
	private MDStoreVersionRepository mdstoreVersionRepository;
	@Autowired
	private MDStoreCurrentVersionRepository mdstoreCurrentVersionRepository;
	@Autowired
	private MDStoreWithInfoRepository mdstoreWithInfoRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Iterable<MDStoreWithInfo> find() {
		return mdstoreWithInfoRepository.findAll();
	}

	@RequestMapping(value = "/ids", method = RequestMethod.GET)
	public List<String> findIdentifiers() {
		return mdstoreRepository.findAll().stream().map(MDStore::getId).collect(Collectors.toList());
	}

	@RequestMapping(value = "/mdstore/{mdId}", method = RequestMethod.GET)
	public MDStoreWithInfo get(@PathVariable final String mdId) throws NoContentException {
		return mdstoreWithInfoRepository.findById(mdId).orElseThrow(() -> new NoContentException("Missing mdstore: " + mdId));
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public long count() {
		return mdstoreRepository.count();
	}

	@RequestMapping(value = "/new/{format}/{layout}/{interpretation}", method = RequestMethod.PUT)
	public MDStoreWithInfo createMDStore(
			@PathVariable final String format,
			@PathVariable final String layout,
			@PathVariable final String interpretation,
			@RequestParam(required = false) final String dsId,
			@RequestParam(required = false) final String apiId) throws MDStoreManagerException {
		final String id = _createMDStore(format, layout, interpretation, dsId, apiId);
		return mdstoreWithInfoRepository.findById(id).orElseThrow(() -> new MDStoreManagerException("MDStore not found"));
	}

	@Transactional
	private String _createMDStore(final String format, final String layout, final String interpretation, final String dsId, final String apiId) {
		final MDStore md = MDStore.newInstance(dsId, apiId, format, layout, interpretation);
		mdstoreRepository.save(md);

		final MDStoreVersion v = MDStoreVersion.newInstance(md.getId(), false);
		v.setLastUpdate(new Date());
		mdstoreVersionRepository.save(v);
		mdstoreCurrentVersionRepository.save(MDStoreCurrentVersion.newInstance(v));

		return md.getId();
	}

	@Transactional
	@RequestMapping(value = "/mdstore/{mdId}", method = RequestMethod.DELETE)
	public void delete(@PathVariable final String mdId) throws MDStoreManagerException {
		if (!mdstoreRepository.existsById(mdId)) { throw new NoContentException("On delete MDStore not found " + "[" + mdId + "]"); }

		if (mdstoreVersionRepository.countByMdstoreAndReadCountGreaterThan(mdId,
				0) > 0) { throw new MDStoreManagerException("Read transactions found on mdstore : " + mdId); }

		if (mdstoreVersionRepository.countByMdstoreAndWriting(mdId,
				true) > 0) { throw new MDStoreManagerException("Write transactions found on mdstore : " + mdId); }

		mdstoreCurrentVersionRepository.deleteById(mdId);
		mdstoreVersionRepository.deleteByMdstore(mdId);
		mdstoreRepository.deleteById(mdId);
	}

	@Transactional
	@RequestMapping(value = "/mdstore/{mdId}/newVersion", method = RequestMethod.GET)
	public MDStoreVersion prepareNewVersion1(@PathVariable final String mdId) {
		final MDStoreVersion v = MDStoreVersion.newInstance(mdId, true);
		mdstoreVersionRepository.save(v);
		return v;
	}

	@Transactional
	@RequestMapping(value = "/version/{versionId}/commit/{size}", method = RequestMethod.GET)
	public void commitVersion(@PathVariable final String versionId, @PathVariable final int size) throws NoContentException {
		final MDStoreVersion v = mdstoreVersionRepository.findById(versionId).orElseThrow(() -> new NoContentException("Invalid version: " + versionId));
		mdstoreCurrentVersionRepository.save(MDStoreCurrentVersion.newInstance(v));
		v.setWriting(false);
		v.setSize(size);
		v.setLastUpdate(new Date());
		mdstoreVersionRepository.save(v);
	}

	@RequestMapping(value = "/versions/expired", method = RequestMethod.GET)
	public List<String> listExpiredVersions() {
		return jdbcTemplate.queryForList(
				"select v.id from mdstore_versions v left outer join mdstore_current_versions cv on (v.id = cv.current_version) where v.writing = false and v.readcount = 0 and cv.mdstore is null;",
				String.class);
	}

	@Transactional
	@RequestMapping(value = "/version/{versionId}", method = RequestMethod.DELETE)
	public void deleteVersion(@PathVariable final String versionId) throws MDStoreManagerException {
		_deleteVersion(versionId);
	}

	@Transactional
	@RequestMapping(value = "/versions/deleteList", method = RequestMethod.POST)
	public void deleteVersions(@RequestBody final List<String> versions) throws MDStoreManagerException {
		for (final String v : versions) {
			_deleteVersion(v);
		}
	}

	private void _deleteVersion(final String versionId) throws MDStoreManagerException {
		final MDStoreVersion v = mdstoreVersionRepository.findById(versionId).orElseThrow(() -> new MDStoreManagerException("Version not found"));

		if (v.isWriting()) { throw new MDStoreManagerException("I cannot delete this version because it is in write mode"); }
		if (v.getReadCount() > 0) { throw new MDStoreManagerException("I cannot delete this version because it is in read mode"); }
		if (mdstoreCurrentVersionRepository
				.countByCurrentVersion(versionId) > 0) { throw new MDStoreManagerException("I cannot delete this version because it is the current version"); }

		mdstoreVersionRepository.delete(v);
	}

}
