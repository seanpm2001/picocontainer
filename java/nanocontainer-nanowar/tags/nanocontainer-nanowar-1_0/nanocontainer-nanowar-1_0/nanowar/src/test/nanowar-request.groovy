pico = builder.container(parent:parent, scope:assemblyScope) {
	component(key:'requestScopedInstance', instance:'foo bar')
	component(key:'testFooHierarchy', class:'org.nanocontainer.nanowar.FooHierarchy')
}