
SNMP4J-AgentX SECURITY README
=============================

AgentX does not implement its own security and since
a AgentX subagent may significantly interfere a master
agent's operation, security measures should be taken
to allow only known sub-agents to connect to the master
agent.

In most cases, you will only want to allow sub-agents
from the localhost to connect to the master agent.
The "master.security" file in this directory can be used
to enforce that by adding the following JVM options to
the command line that starts the master agent:

-Djava.security.manager=java.lang.SecurityManager
-Djava.security.policy=master.security

Alternatively, or better in addition, you can configure
AgentXMasterAgent to allow sub-agent connections from
the localhost only, by calling

myAgentXMasterAgent.setLocalhostSubagentsOnly(true)
