def String call(String Recipient){
  String[] RecipientGroup;
  String Recipients;
  String RecipientList="";
  RecipientGroup=Recipient.split(';');

  //get email id from all groups name
  for(String values : RecipientGroup){
    //read from github so profile of all users must be updated in GitHub
    Recipients=sh(script: """curl -L -H "Accept: application/vnd.github+json" -H "Authorization: Bearer $GHub_Access(patToken)" -H "X-GitHub-Api-Version: 2022-11-28" https://gitUrl/api/v3/orgs/OrgName/teams/$values/members | grep -E '"url":' | grep -o -E 'https?://[^"]+' | xargs -L1 curl -L -H "Accept: application/vnd.github+json" -H "Authorization: Bearer $GHub_Access(patToken)" -H "X-GitHub-Api-Version: 2022-11-28" | grep -oe "[a-zAA-Z0-9._-]\\+@[a-zA-Z]\\+.[a-zA-Z]\\+" | while read line; do echo \$line; done | tr '\\n' ";" """,returnStdOut: true).trim()

    //group wise email IDs will be concatenate to create list
    RecipientList= RecipientList + Recipients;
  }
  return "${RecipientList}"
}
