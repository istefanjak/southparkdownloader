package com.istef.southpark.repo;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.istef.southpark.exception.RepoLoadException;
import com.istef.southpark.model.json.RepoModel;
import com.istef.southpark.model.json.RepoModel.Season;
import com.istef.southpark.model.json.RepoModel.Season.Mgid;

public class RepoTest extends Repo {

	public RepoTest(Path relativePath) throws RepoLoadException {
		super(relativePath);
	}
	
	@Override
	protected RepoModel initRepoModel() {
		RepoModel m = new RepoModel();
		m.name = "My Test repo";
		m.description = "My Test repo desc";

		Season season = new Season();
		season.name = "Season 23";
		List<Mgid> mgids = Arrays.asList(
				new Mgid("mgid:arc:episode:southparkstudios.com:ac8de693-b355-11e9-9fb2-70df2f866ace"),
				new Mgid("mgid:arc:episode:southparkstudios.com:ac8de7f5-b355-11e9-9fb2-70df2f866ace"),
				new Mgid("mgid:arc:episode:southparkstudios.com:ac8de8da-b355-11e9-9fb2-70df2f866ace"),
				new Mgid("mgid:arc:episode:southparkstudios.com:ac8de968-b355-11e9-9fb2-70df2f866ace"));
		season.mgids = mgids;

		m.seasons = Arrays.asList(season);
		return m;
	}

}
