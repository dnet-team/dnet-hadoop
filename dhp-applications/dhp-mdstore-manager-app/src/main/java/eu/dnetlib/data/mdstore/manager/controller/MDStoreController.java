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

import com.google.common.collect.Lists;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/mdstores")
@Api(tags = { "Metadata Stores" })
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

	@ApiOperation(value = "Return all the mdstores")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<MDStoreWithInfo> find() {
		return Lists.newArrayList(mdstoreWithInfoRepository.findAll());
	}

	@ApiOperation(value = "Return all the mdstore identifiers")
	@RequestMapping(value = "/ids", method = RequestMethod.GET)
	public List<String> findIdentifiers() {
		return mdstoreRepository.findAll().stream().map(MDStore::getId).collect(Collectors.toList());
	}

	@ApiOperation(value = "Return a mdstores by id")
	@RequestMapping(value = "/mdstore/{mdId}", method = RequestMethod.GET)
	public MDStoreWithInfo get(@ApiParam("the mdstore identifier") @PathVariable final String mdId) throws NoContentException {
		return mdstoreWithInfoRepository.findById(mdId).orElseThrow(() -> new NoContentException("Missing mdstore: " + mdId));
	}

	@ApiOperation(value = "Return the number of mdstores")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public long count() {
		return mdstoreRepository.count();
	}

	@ApiOperation(value = "Create a new mdstore")
	@RequestMapping(value = "/new/{format}/{layout}/{interpretation}", method = RequestMethod.PUT)
	public MDStoreWithInfo createMDStore(
			@ApiParam("mdstore format") @PathVariable final String format,
			@ApiParam("mdstore layout") @PathVariable final String layout,
			@ApiParam("mdstore interpretation") @PathVariable final String interpretation,
			@ApiParam("datasource id") @RequestParam(required = false) final String dsId,
			@ApiParam("api id") @RequestParam(required = false) final String apiId) throws MDStoreManagerException {
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
	@ApiOperation(value = "Delete a mdstore by id")
	@RequestMapping(value = "/mdstore/{mdId}", method = RequestMethod.DELETE)
	public void delete(@ApiParam("the id of the mdstore that will be deleted") @PathVariable final String mdId) throws MDStoreManagerException {
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
	@ApiOperation(value = "Create a new preliminary version of a mdstore")
	@RequestMapping(value = "/mdstore/{mdId}/newVersion", method = RequestMethod.GET)
	public MDStoreVersion prepareNewVersion(@ApiParam("the id of the mdstore for which will be created a new version") @PathVariable final String mdId) {
		final MDStoreVersion v = MDStoreVersion.newInstance(mdId, true);
		mdstoreVersionRepository.save(v);
		return v;
	}

	@Transactional
	@ApiOperation(value = "Promote a preliminary version to current")
	@RequestMapping(value = "/version/{versionId}/commit/{size}", method = RequestMethod.GET)
	public void commitVersion(@ApiParam("the id of the version that will be promoted to the current version") @PathVariable final String versionId,
			@ApiParam("the size of the new current mdstore") @PathVariable final int size) throws NoContentException {
		final MDStoreVersion v = mdstoreVersionRepository.findById(versionId).orElseThrow(() -> new NoContentException("Invalid version: " + versionId));
		mdstoreCurrentVersionRepository.save(MDStoreCurrentVersion.newInstance(v));
		v.setWriting(false);
		v.setSize(size);
		v.setLastUpdate(new Date());
		mdstoreVersionRepository.save(v);
	}

	@ApiOperation(value = "Return all the expired versions of all the mdstores")
	@RequestMapping(value = "/versions/expired", method = RequestMethod.GET)
	public List<String> listExpiredVersions() {
		return jdbcTemplate.queryForList(
				"select v.id from mdstore_versions v left outer join mdstore_current_versions cv on (v.id = cv.current_version) where v.writing = false and v.readcount = 0 and cv.mdstore is null;",
				String.class);
	}

	@Transactional
	@ApiOperation(value = "Delete a mdstore version")
	@RequestMapping(value = "/version/{versionId}", method = RequestMethod.DELETE)
	public void deleteVersion(@ApiParam("the id of the version that has to be deleted") @PathVariable final String versionId,
			@ApiParam("if true, the controls on writing and readcount values will be skipped") @RequestParam(required = false, defaultValue = "false") final boolean force)
			throws MDStoreManagerException {
		_deleteVersion(versionId, force);
	}

	@Transactional
	@ApiOperation(value = "Delete multiple mdstore versions")
	@RequestMapping(value = "/versions/deleteList", method = RequestMethod.POST)
	public void deleteVersions(@ApiParam("the list of ids of the versions that have to be deleted") @RequestBody final List<String> versions,
			@ApiParam("if true, the controls on writing and readcount values will be skipped") @RequestParam(required = false, defaultValue = "false") final boolean force)
			throws MDStoreManagerException {
		for (final String v : versions) {
			_deleteVersion(v, force);
		}
	}

	private void _deleteVersion(final String versionId, final boolean force) throws MDStoreManagerException {
		final MDStoreVersion v = mdstoreVersionRepository.findById(versionId).orElseThrow(() -> new MDStoreManagerException("Version not found"));

		if (mdstoreCurrentVersionRepository
				.countByCurrentVersion(versionId) > 0) { throw new MDStoreManagerException("I cannot delete this version because it is the current version"); }

		if (!force) {
			if (v.isWriting()) { throw new MDStoreManagerException("I cannot delete this version because it is in write mode"); }
			if (v.getReadCount() > 0) { throw new MDStoreManagerException("I cannot delete this version because it is in read mode"); }
		}

		mdstoreVersionRepository.delete(v);
	}

}
