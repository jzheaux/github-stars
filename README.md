# GitHub Stars

To get your GitHub stars by month, you can do:

```bash
./gradlew :bootRun \
  -Dproject.name=$PROJ \
  -Dpersonal.access.token=$PAT
```

where `PROJ` is the GitHub project name and `PAT` is a [GitHub personal access token](https://github.com/settings/tokens).

The output is a tab-delimited output of the monthly 2020 historical star count for that project.

The organization is `spring-projects` by default; however, you can override this:

```bash
./gradlew :bootRun \
  -Dproject.name=$PROJ \
  -Dorganization.name=$ORG \
  -Dpersonal.access.token=$PAT
```

You can also run without a personal access token, though GitHub's rate limit is much more strict without one.