#Andy CD
Continuous Delivery Platform with Governance

Each Pipeline has a git repo, you push WAR (or you run a script to build). Either you run your on your infrastructure or you use our multitenant service that runs in a docker container.

Uses GitHub as identity provider.

Each Pipeline is described in JSON file .cd.json(?).
Owner can change the release path, only owners can add owners, when creating new pipeline, you are the only owner.

```json
{
	"name": "Ondrej Stastny App",
	"setup": "deploy/java/maven_compile.sh",
	"owners": [ "ondrej", "thomas"], 
	"env": [
	{
		"name": "Dev",
		"machines": [
		{
			"name": "Heroku",
			"setup": "deploy/heroku/deploy_war.sh",
			"revert": null,
			"approval": [ "ondrej" ]
		}
		],
		"go_to": "Prod"
	},
	{
		"name": "Prod",
		"machines": [
		{
			"name": "Docker Azure",
			"setup": "deploy/docker/build.sh",
			"revert": null,
			"accept": [ "ondrej" ],
			"verify": [ "ondrej" ]
		}
		]
	}
	]
}
```

Some of the API endpoints:

Endpoint | Description
--- | --- 
/users	|		
/logs			 | logs of all operations (setup, revert scripts)
/releases	|	individual releases triggered by a checkin
/params	|	  secure encrypted param store to store pwds etc.
/approvals | approve releases

Different environments and commands are just scipt files in your repo. Also, we provide a gallery of ready-made scripts ("a cookbook")
